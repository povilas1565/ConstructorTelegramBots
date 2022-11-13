import React from "react";
import {DiagramEngine} from "@projectstorm/react-diagrams-core";
import {TrayWidget} from "./TrayWidget";
import {TrayItemWidget} from "./TrayItemWidget";
import {DemoCanvasWidget} from "./DemoCanvasWidget";
import {CanvasWidget} from "@projectstorm/react-canvas-core";
import styled from "@emotion/styled";
import _ from "lodash";
import {DefaultLinkModel, DefaultNodeModel, NodeModel, PortModel} from "@projectstorm/react-diagrams";
import conlose from "pathfinding/visual/lib/async.min";
import {message} from "antd";


export interface BotStagesProps {
    diagram: DiagramEngine;
    stages: Object;
    types: Array;
    saveStage: () => Promise<Object>;
    clearError: () => void;
    botId: Number;
}

export const S = {
    Layer: styled.div`
		position: relative;
		flex-grow: 1;
	`,
    Content: styled.div`
		display: flex;
		flex-grow: 1;
	`,
    Body: styled.div`
		flex-grow: 1;
		display: flex;
		flex-direction: column;
		height: 100%;
	`,
    Header: styled.div`
		background: rgb(30, 30, 30);
		flex-grow: 0;
		flex-shrink: 0;
		color: white;
		font-family: Helvetica, Arial, sans-serif;
		padding: 10px;
		align-items: center;
	`,
}

export const StageType = {
    START: {
        name: "Старт",
        type: "Start",
        color: 'rgb(49,255,0)',
    },
    END: {
        name: "Конец",
        type: "End",
        color: 'rgb(255,0,0)',
    },
    STAGE: {
        name: "Этап",
        type: "Stage",
        color: 'rgb(101,171,29)',
    },
    KEYBOARD: {
        name: "Клавиатура",
        type: "Keyboard",
        color: 'rgb(30,87,152)',
    },
    BUTTON: {
        name: "Кнопка",
        type: "Button",
        color: "rgb(119,88,2)",
    },
    SCHEDULE: {
        name: "Этап-уведомление",
        type: "Schedule",
        color: 'rgb(0,98,101)',
    },

    getAll(): string[] {
        return [StageType.START, StageType.END, StageType.STAGE, StageType.SCHEDULE];
    }
}



export class BotStages extends React.Component<BotStagesProps> {
    constructor(props) {
        super(props);
        this.models = StageType.getAll().map(type => {
            return <TrayItemWidget model={{ type: type }} name={type.name} color={type.color} />
        });
        this.state = {
            loading: false,
            isMenuOpen: false,
            selectedNode: new DefaultNodeModel(),
            //TODO: Клава и кнопки
            keyboards: {},
            buttons: {},
        };
        this.props.diagram.getModel().getNodes().forEach(node => {
            this.registryListener(node);
        });
    }

    registryListener = (node: NodeModel) => {
        node.registerListener({
            selectionChanged: () => {
                if (node.getOptions().extras.ident !== StageType.START.type
                    && node.getOptions().extras.ident !== StageType.END.type
                ) {
                    this.setState({
                        isMenuOpen: true,
                        selectedNode: node,
                    });
                } else {
                    this.setState({
                        isMenuOpen: false,
                        selectedNode: null,
                    });
                }
            },
            entityRemoved: () => {
                if (node.getOptions().extras.ident === StageType.KEYBOARD.type) {
                    Object.values(node.getPort("Buttons").getLinks()).forEach(link => {
                        this.props.diagram.getModel().removeNode(link.getTargetPort().getNode());
                        this.props.diagram.getModel().removeLink(link);
                    });

                    const localNode = Object.values(node.getPort("In").getLinks())[0].getSourcePort().getNode()
                    delete this.state.keyboards[localNode.getID()];
                }
                if (node.getOptions().extras.ident === StageType.BUTTON.type) {
                    delete this.state.buttons[node.getID()];
                }
                if (node.getOptions().extras.ident === StageType.STAGE.type || node.getOptions().extras.ident === StageType.SCHEDULE.type) {
                    Object.values(node.getPort("Keyboards").getLinks()).forEach(link => {
                        const localNode = link.getTargetPort().getNode();
                        Object.values(localNode.getPort("Buttons").getLinks()).forEach(button => {
                            Object.values(button.getTargetPort().getNode().getPort("Out").getLinks()).forEach(link => {
                                this.props.diagram.getModel().removeLink(link);
                            })
                            this.props.diagram.getModel().removeNode(button.getTargetPort().getNode());
                            delete this.state.buttons[localNode.getID()];
                            this.props.diagram.getModel().removeLink(button);
                        });
                        this.props.diagram.getModel().removeNode(localNode);
                        delete this.state.keyboards[node.getID()];
                        this.props.diagram.getModel().removeLink(link);
                    });
                }
                this.setState({
                    isMenuOpen: false,
                    selectedNode: null,
                });
            },
        });
    }

    doubleClick = (event: React.MouseEvent<HTMLDivElement>) => {
        conlose.log(event.currentTarget);
    }

    onDrop(event: any) {
        const data = JSON.parse(event.dataTransfer.getData('storm-diagram-node'));
        const nodesCount = _.keys(this.props.diagram.getModel().getNodes()).length;

        let node: DefaultNodeModel = null;
        if (data.type.type === StageType.START.type) {
            node = new DefaultNodeModel({name: data.type.name, color: data.type.color, extras: {ident: data.type.type, fictiveId: -Math.abs(nodesCount + 1)}});
            node.addOutPort('Out');
        } else if (data.type.type === StageType.END.type) {
            node = new DefaultNodeModel({name: data.type.name, color: data.type.color, extras: {ident: data.type.type, fictiveId: -Math.abs(nodesCount + 1)}});
            node.addInPort('In');
        } else if (data.type.type === StageType.KEYBOARD.type) {
            node = new DefaultNodeModel({
               name: data.type.name + " " + nodesCount,
               color: data.type.color,
               extras: {
                   ident: data.type.type
               }
            });
            node.addInPort('In');
            node.addOutPort('Out');
        } else {
            node = new DefaultNodeModel({
                name: data.type.name  + " " + nodesCount,
                color: data.type.color,
                extras: {
                    ident: data.type.type,
                    fictiveId: -Math.abs(nodesCount + 1),
                    id: null,
                }
            });
            //node.updateDimensions({width: 50, height: 50});
            node.addInPort('In');
            node.addOutPort('Out');
            node.getOutPorts()[0].setMaximumLinks(1);

            const port = new PortModel({name: "Keyboards", alignment: "right", label: "Keyboards", type: 'default'});
            port.setMaximumLinks(1);
            node.addPort(port);
            this.registryListener(node);
            node.getOptions().extras.id = "telegram_stage_id" + node.getID();
        }
        const point = this.props.diagram.getRelativeMousePoint(event);
        node.setPosition(point);
        this.props.diagram.getModel().addNode(node);
        this.forceUpdate();
    }

  render() {
        const findNode = ({nodes, nodeId, previousNodeId, back, len}) => {
            const node: DefaultNodeModel = nodes[nodeId];
            if (node.getOptions().extras.ident === StageType.END.type) {
                return;
            }

            if (node.getOptions().extras.ident === StageType.START.type) {
                Object.values(node.getPort('Out').getLinks()).forEach(start => {
                    const nextId = start.getTargetPort().getParent().getID();
                    findNode({nodes, nodeId: nextId, previousNodeId: null, back, len: null});
                });
                return;
            }

            if (node.getOptions().extras.ident === StageType.KEYBOARD.type) {
                const len = back.length - 1;
                Object.values(node.getPort('Buttons').getLinks()).forEach(link => {
                    const nextId = link.getTargetPort().getParent().getID();
                    const dict = back[len];
                    if (dict['telegram_keyboards'] === undefined) {
                        dict['telegram_keyboards'] = [{
                            id: /^\d+$/.test(node.getOptions().extras.id) ? Number(node.getOptions().extras.id) : null,
                            telegram_keyboard_type: node.getOptions().extras.keyboardType,
                            front_node_id: node.getID(),
                        }];
                        back[len] = dict;
                    }
                    findNode({nodes, nodeId: nextId, previousNodeId, back, len});
                });
                return;
            }

            if (node.getOptions().extras.ident === StageType.BUTTON.type) {
                Object.values(node.getPort('Out').getLinks()).forEach(link => {
                    const nextId = link.getTargetPort().getParent().getID();
                    let dict = back[len];
                    dict = dict['telegram_keyboards'][0];
                    if (dict['telegram_keyboard_rows'] === undefined) {
                        dict['telegram_keyboard_rows'] = [];
                        dict['telegram_keyboard_rows'][Number(node.getOptions().extras.buttonRowOrd)-1] = {
                            ord: node.getOptions().extras.buttonRowOrd ? Number(node.getOptions().extras.buttonRowOrd) : 1,
                            telegram_buttons: [],
                            id: /^\d+$/.test(node.getOptions().extras.rowId) ? Number(node.getOptions().extras.rowId) : null,
                        };
                    } else {
                        if (dict['telegram_keyboard_rows'][node.getOptions().extras.buttonRowOrd ?
                            Number(node.getOptions().extras.buttonRowOrd) - 1 : 0] === undefined) {
                            dict['telegram_keyboard_rows'][node.getOptions().extras.buttonRowOrd ?
                                Number(node.getOptions().extras.buttonRowOrd) - 1 : 0] = {
                                ord: node.getOptions().extras.buttonRowOrd ? Number(node.getOptions().extras.buttonRowOrd) : 1,
                                telegram_buttons: [],
                                id: /^\d+$/.test(node.getOptions().extras.rowId) ? Number(node.getOptions().extras.rowId) : null,
                            }
                        }
                    }
                    dict = dict['telegram_keyboard_rows'][Number(node.getOptions().extras.buttonRowOrd)-1]['telegram_buttons'];
                    dict[dict.length] = {
                        id: /^\d+$/.test(node.getOptions().extras.id) ? Number(node.getOptions().extras.id) : null,
                        button_text: node.getOptions().extras.buttonText ? node.getOptions().extras.buttonText : null,
                        button_link: node.getOptions().extras.buttonLink ? node.getOptions().extras.buttonLink : null,
                        button_ord: node.getOptions().extras.buttonOrd ? Number(node.getOptions().extras.buttonOrd) : null,
                        callback_data: {
                            id: /^\d+$/.test(nodes[nextId].getOptions().extras.id) ? Number(nodes[nextId].getOptions().extras.id) : null,
                            fictive_id: nodes[nextId].getOptions().extras.fictiveId
                        },
                        front_node_id: node.getID(),
                    };
                    findNode({nodes, nodeId: nextId, previousNodeId, back, len: null});
                });
                if (Object.values(node.getPort('Out').getLinks()).length === 0) {
                    let dict = back[len];
                    dict = dict['telegram_keyboards'][0];
                    if (dict['telegram_keyboard_rows'] === undefined) {
                        dict['telegram_keyboard_rows'] = [];
                        dict['telegram_keyboard_rows'][Number(node.getOptions().extras.buttonRowOrd)-1] = {
                            ord: node.getOptions().extras.buttonRowOrd ? Number(node.getOptions().extras.buttonRowOrd) : 1,
                            telegram_buttons: [],
                            id: /^\d+$/.test(node.getOptions().extras.rowId) ? Number(node.getOptions().extras.rowId) : null,
                        };
                    } else {
                        if (dict['telegram_keyboard_rows'][node.getOptions().extras.buttonRowOrd ?
                            Number(node.getOptions().extras.buttonRowOrd) - 1 : 0] === undefined) {
                            dict['telegram_keyboard_rows'][node.getOptions().extras.buttonRowOrd ?
                                Number(node.getOptions().extras.buttonRowOrd) - 1 : 0] = {
                                ord: node.getOptions().extras.buttonRowOrd ? Number(node.getOptions().extras.buttonRowOrd) : 1,
                                telegram_buttons: [],
                                id: /^\d+$/.test(node.getOptions().extras.rowId) ? Number(node.getOptions().extras.rowId) : null,
                            }
                        }
                    }
                    dict = dict['telegram_keyboard_rows'][Number(node.getOptions().extras.buttonRowOrd)-1]['telegram_buttons'];
                    dict[dict.length] = {
                        id: /^\d+$/.test(node.getOptions().extras.id) ? Number(node.getOptions().extras.id) : null,
                        button_text: node.getOptions().extras.buttonText ? node.getOptions().extras.buttonText : null,
                        button_link: node.getOptions().extras.buttonLink ? node.getOptions().extras.buttonLink : null,
                        button_ord: node.getOptions().extras.buttonOrd ? Number(node.getOptions().extras.buttonOrd) : null,
                        callback_data: {
                            id: null,
                            fictive_id: null
                        },
                        front_node_id: node.getID(),
                    };
                }
                return;
            }

            if (node.getOptions().extras.ident === StageType.STAGE.type || node.getOptions().extras.ident === StageType.SCHEDULE.type) {
                let check = false;
                back.forEach(stage => {
                    if (stage.front_node_id === node.getID()) {
                        check = true;
                    }
                });
                if (!check) {
                    Object.values(node.getPort('Keyboards').getLinks()).forEach(link => {
                        const nextId = link.getTargetPort().getParent().getID();
                        back[back.length] = {
                            id: /^\d+$/.test(node.getOptions().extras.id) ? Number(node.getOptions().extras.id) : null,
                            fictive_id: node.getOptions().extras.fictiveId,
                            name: node.getOptions().name,
                            previous_stage: previousNodeId,
                            telegram_messages: [{
                                id: /^\d+$/.test(node.getOptions().extras.msgId) ? Number(node.getOptions().extras.msgId) : null,
                                message_text: node.getOptions().extras.message
                            }],
                            front_prefix_replace: "telegram_stage_id" + node.getID(),
                            front_node_id: node.getID(),
                            is_schedule_active: node.getOptions().extras.ident === StageType.SCHEDULE.type,
                            schedule_cron: node.getOptions().extras.ident === StageType.SCHEDULE.type ? node.getOptions().extras.cron : null,
                            schedule_date_time: node.getOptions().extras.ident === StageType.SCHEDULE.type ? node.getOptions().extras.datetimeUTC : null,
                        }
                        findNode({
                            nodes,
                            nodeId: nextId,
                            previousNodeId: node.getOptions().extras.fictiveId,
                            back,
                            len: null
                        });
                    });
                    if (Object.values(node.getPort('Keyboards').getLinks()).length === 0) {
                        back[back.length] = {
                            id: /^\d+$/.test(node.getOptions().extras.id) ? Number(node.getOptions().extras.id) : null,
                            fictive_id: node.getOptions().extras.fictiveId,
                            name: node.getOptions().name,
                            previous_stage: previousNodeId,
                            telegram_messages: [{
                                id: /^\d+$/.test(node.getOptions().extras.msgId) ? Number(node.getOptions().extras.msgId) : null,
                                message_text: node.getOptions().extras.message
                            }],
                            front_prefix_replace: "telegram_stage_id" + node.getID(),
                            front_node_id: node.getID(),
                            is_schedule_active: node.getOptions().extras.ident === StageType.SCHEDULE.type,
                            schedule_cron: node.getOptions().extras.ident === StageType.SCHEDULE.type ? node.getOptions().extras.cron : null,
                            schedule_date_time: node.getOptions().extras.ident === StageType.SCHEDULE.type ? node.getOptions().extras.datetimeUTC : null,
                        };

                    }
                }
            }
        }

      const onSave = async () => {
          const model = this.props.diagram.getModel();
          const nodes = {};
          const startNode = model.getNodes().find(node => node.getOptions().extras.ident === StageType.START.type);
          startNode.updateDimensions({width: 50, height: 50});

          //Собираем мапу всех узлов
          model.getNodes().forEach(node => {
              nodes[node.getID()] = node
          });

          const nodesForBack = [];

          findNode({nodes, nodeId: startNode.getID(), previousNodeId: null, back: nodesForBack, len: null});

          const data = {
              telegram_stages: nodesForBack,
              front_options: JSON.stringify(this.props.diagram.getModel().serialize()),
          };

          let returnData;

          try {
              returnData = await this.props.saveStage(data, this.props.botId);
              message.success('Этапы сохранены!', [5]);
          } catch (e) {
              message.error(e.message || 'Что-то пошло не так', [5]);
              this.props.clearError();
          }

          this.props.diagram.getModel().getNodes().forEach(node => {
              returnData.telegram_stages.forEach(stage => {
                  if (node.getID() === stage.front_node_id) {
                      node.getOptions().extras.id = stage.id;
                      stage.telegram_messages.forEach(msg => {
                        node.getOptions().extras.msgId = msg.id;
                      });
                  }
                  stage.telegram_keyboards.forEach(keyboard => {
                      if (node.getID() === keyboard.front_node_id) {
                          node.getOptions().extras.id = keyboard.id;
                      }
                      keyboard.telegram_keyboard_rows.forEach(row => {
                          row.telegram_buttons.forEach(btn => {
                              if (node.getID() === btn.front_node_id) {
                                  node.getOptions().extras.rowId = row.id;
                                  node.getOptions().extras.id = btn.id;
                              }
                          });
                      });
                  });
              });
          })
      }

      const onChangeOptions = (event) => {
            const newNode = this.state.selectedNode;
            if (event.target.name === 'name') {
                newNode.getOptions()[event.target.name] = event.target.value;
            } else {
                if (event.target.name === 'keyboardType') {
                    newNode.getOptions().extras[event.target.name] = this.props.types.find(type => {
                        if (type.name === event.target.value) {
                            return type;
                        }
                    });
                } else if (event.target.name === 'datetime') {
                    newNode.getOptions().extras['datetimeUTC'] = new Date(event.target.value).toJSON();
                    newNode.getOptions().extras[event.target.name] = event.target.value;
                    newNode.getOptions().extras['cron'] = null;
                } else if (event.target.name === 'cron') {
                    newNode.getOptions().extras[event.target.name] = event.target.value;
                    newNode.getOptions().extras['datetimeUTC'] = null;
                } else {
                    newNode.getOptions().extras[event.target.name] = event.target.value;
                }
            }
            this.setState({
                selectedNode: newNode,
            });
      }

      const onChangeLock = (event) => {
          this.state.selectedNode.setLocked(true);
      }

      const onChangeLockFalse = (event) => {
          this.state.selectedNode.setLocked(false);
          if (this.props.diagram.getModel().getSelectedEntities().length === 0) {
              this.setState({
                  isMenuOpen: false,
                  selectedNode: null,
              });
          }
      }

      const addKeyboard = (event) => {
            event.preventDefault();
            const nodesCount = _.keys(this.props.diagram.getModel().getNodes()).length;
            const node: DefaultNodeModel = new DefaultNodeModel({
                name: StageType.KEYBOARD.name + " " + nodesCount,
                color: StageType.KEYBOARD.color,
                extras: {
                    ident: StageType.KEYBOARD.type
                }
            });
            node.addInPort('In');
            node.addOutPort('Buttons');
            node.getInPorts()[0].setMaximumLinks(1);

            //TODO: Позиционирование надо пофиксить
            const point = this.state.selectedNode.getPosition().clone();
            point.y -= 100
            point.x += 100
            node.setPosition(point);

            const link = new DefaultLinkModel();
            link.setSourcePort(this.state.selectedNode.getPort('Keyboards'));
            link.setTargetPort(node.getPort('In'));

            this.registryListener(node);
            this.props.diagram.getModel().addAll(node, link);
            this.forceUpdate();

            const keyboards = this.state.keyboards;
            let globalDict = keyboards[this.state.selectedNode.getID()];
            if (globalDict === undefined) {
                globalDict = {}
                globalDict[node.getID()] = <tr id={link.getID()}><td>{node.getOptions().name}</td></tr>;
            } else {
                globalDict[node.getID()] = <tr id={link.getID()}><td>{node.getOptions().name}</td></tr>;
            }

            keyboards[this.state.selectedNode.getID()] = globalDict;
      }

      const addButton = (event) => {
          event.preventDefault();
          const nodesCount = _.keys(this.props.diagram.getModel().getNodes()).length;
          const node: DefaultNodeModel = new DefaultNodeModel({
              name: StageType.BUTTON.name + " " + nodesCount,
              color: StageType.BUTTON.color,
              extras: {
                  ident: StageType.BUTTON.type
              }
          });
          node.addInPort('In');
          node.addOutPort('Out');
          node.getInPorts()[0].setMaximumLinks(1);
          node.getOutPorts()[0].setMaximumLinks(1);

          //TODO: Позиционирование надо пофиксить
          const point = this.state.selectedNode.getPosition().clone();
          point.y -= 100
          point.x += 100
          node.setPosition(point);

          const link = new DefaultLinkModel();
          link.setSourcePort(this.state.selectedNode.getPort('Buttons'))
          link.setTargetPort(node.getPort('In'))

          this.registryListener(node);
          this.props.diagram.getModel().addAll(node, link);
          this.forceUpdate();

          const buttons = this.state.buttons;
          let globalDict = buttons[this.state.selectedNode.getID()];
          if (globalDict === undefined) {
              globalDict = {}
              globalDict[node.getID()] = <tr id={link.getID()}><td>{node.getOptions().name}</td></tr>;
          } else {
              globalDict[node.getID()] = <tr id={link.getID()}><td>{node.getOptions().name}</td></tr>;
          }

          buttons[this.state.selectedNode.getID()] = globalDict;
      }

    return (
        <S.Body>
            <S.Header>
                <div>
                    bot name
                    <button className="btn green darken-2" style={{float: "right"}} onClick={onSave} disabled={this.state.loading}>Сохранить</button>
                </div>
            </S.Header>
            <S.Content>
                <TrayWidget>
                    {this.models}
                </TrayWidget>
                <S.Layer onClick={onChangeLockFalse}
                    onDrop={(event) => this.onDrop(event)}
                    onDragOver={(event) => {
                        event.preventDefault();
                    }}>
                    <DemoCanvasWidget>
                        <CanvasWidget engine={this.props.diagram} />
                    </DemoCanvasWidget>
                </S.Layer>
                {this.state.isMenuOpen && (this.state.selectedNode.getOptions().extras.ident === StageType.STAGE.type || this.state.selectedNode.getOptions().extras.ident === StageType.SCHEDULE.type) ? (
                    <div className="row">
                        <div className="col s24" style={{height: '100%'}}>
                            <div className="card grey lighten-4"  style={{height: '100%'}}>
                                <div className="card-content black-text">
                                    <span className="card-title" style={{fontWeight: "bold"}}>Настройки этапа</span>
                                    <form onClick={onChangeLock}>
                                        <form-group>
                                            <label>Название</label>
                                            <input type="text" name='name' value={this.state.selectedNode.getOptions().name || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        <form-group>
                                            <label>Сообщение</label>
                                            <textarea style={{resize: "none", height: 150}} name='message' value={this.state.selectedNode.getOptions().extras.message || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        {this.state.selectedNode.getOptions().extras.ident === StageType.SCHEDULE.type ? (
                                            <form-group>
                                                <label>Многократное повторение?</label>
                                                <select name="executeManyTimes" id="executeManyTimes"
                                                        value={this.state.selectedNode.getOptions().extras.executeManyTimes || ''}
                                                        onChange={onChangeOptions} style={{display: "block"}}>
                                                    <option value="null"></option>
                                                    <option value="Да">Да</option>
                                                    <option value="Нет">Нет</option>
                                                </select>
                                            </form-group>
                                        ) : null}
                                        {this.state.selectedNode.getOptions().extras.ident === StageType.SCHEDULE.type
                                        && this.state.selectedNode.getOptions().extras.executeManyTimes === 'Нет' ? (
                                            <form-group>
                                                <label>Дата</label>
                                                <input type="datetime-local" name='datetime' value={this.state.selectedNode.getOptions().extras.datetime || ''}
                                                       onChange={onChangeOptions}/>
                                            </form-group>
                                        ) : this.state.selectedNode.getOptions().extras.ident === StageType.SCHEDULE.type
                                        && this.state.selectedNode.getOptions().extras.executeManyTimes === 'Да' ? (
                                            <form-group>
                                                <label>Крон</label>
                                                <input type="text" name='cron' value={this.state.selectedNode.getOptions().extras.cron || ''}
                                                       placeholder='секунду минута час день месяц день_в_недели'
                                                       onChange={onChangeOptions}/>
                                            </form-group>
                                        ): null}
                                        <form-group>
                                            <label>Клавиатура</label>
                                            <br/>
                                            {this.state.keyboards[this.state.selectedNode.getID()] !== undefined ? (
                                                <table className="highlight">
                                                    <tbody>
                                                    {Object.values(this.state.keyboards[this.state.selectedNode.getID()])}
                                                    </tbody>
                                                </table>
                                            ) : <button className="waves-effect waves-light btn" onClick={addKeyboard} disabled={this.state.loading}>Добавить клавиатуру</button>}
                                        </form-group>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    ) : this.state.isMenuOpen && this.state.selectedNode.getOptions().extras.ident === StageType.KEYBOARD.type ? (
                    <div className="row">
                        <div className="col s24" style={{height: '100%'}}>
                            <div className="card grey lighten-4"  style={{height: '100%'}}>
                                <div className="card-content black-text">
                                    <span className="card-title" style={{fontWeight: "bold"}}>Настройки клавиатуры</span>
                                    <form onClick={onChangeLock}>
                                        <form-group>
                                            <label>Название</label>
                                            <input type="text" name='name' value={this.state.selectedNode.getOptions().name || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        <form-group>
                                            <label className="black-text active" htmlFor="is_active">Тип клавиатуры</label>
                                            <select name="keyboardType" id="keyboardType"
                                                    value={this.state.selectedNode.getOptions().extras.keyboardType ? this.state.selectedNode.getOptions().extras.keyboardType.name : ''}
                                                    onChange={onChangeOptions} style={{display: "block"}}>
                                                <option value="null"></option>
                                                <option value="InlineKeyboardMarkup">InlineKeyboardMarkup</option>
                                                <option value="ReplyKeyboardMarkup">ReplyKeyboardMarkup</option>
                                            </select>
                                        </form-group>
                                        <br/>
                                        <form-grop>
                                            <label>Кнопки</label>
                                            <br/>
                                            <button className="waves-effect waves-light btn" onClick={addButton} disabled={this.state.loading}>Добавить кнопку</button>
                                            {this.state.buttons[this.state.selectedNode.getID()] !== undefined ? (
                                                <table className="highlight">
                                                    <tbody>
                                                    {Object.values(this.state.buttons[this.state.selectedNode.getID()])}
                                                    </tbody>
                                                </table>
                                            ): null}
                                        </form-grop>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                ) : this.state.isMenuOpen && this.state.selectedNode.getOptions().extras.ident === StageType.BUTTON.type ? (
                    <div className="row" style={{width: '25%'}}>
                        <div className="col s24" style={{height: '100%'}}>
                            <div className="card grey lighten-4"  style={{height: '100%'}}>
                                <div className="card-content black-text">
                                    <span className="card-title" style={{fontWeight: "bold"}}>Настройки кнопки</span>
                                    <form onClick={onChangeLock}>
                                        <form-group>
                                            <label>Название</label>
                                            <input type="text" name='name' value={this.state.selectedNode.getOptions().name || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        <form-group>
                                            <label>Текст</label>
                                            <input type="text" name='buttonText' value={this.state.selectedNode.getOptions().extras.buttonText || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        <form-group>
                                            <label>Ссылка</label>
                                            <input type="text" name='buttonLink' value={this.state.selectedNode.getOptions().extras.buttonLink || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        <form-group>
                                            <label>Порядок кнопки в столбце</label>
                                            <input type="number" name='buttonRowOrd' value={this.state.selectedNode.getOptions().extras.buttonRowOrd || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                        <form-group>
                                            <label>Порядок кнопки в строке</label>
                                            <input type="number" name='buttonOrd' value={this.state.selectedNode.getOptions().extras.buttonOrd || ''}
                                                   onChange={onChangeOptions}/>
                                        </form-group>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                ) : null}
            </S.Content>
        </S.Body>
    );
  }
}