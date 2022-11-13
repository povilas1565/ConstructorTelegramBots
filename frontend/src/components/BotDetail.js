import React, {useCallback, useState} from "react";
import PropTypes from "prop-types";
import {BotApi} from "../api/BotApi";
import {Link, useNavigate} from "react-router-dom";
import {message} from "antd";


export const BotDetail = ({ bot_ }) => {
  const [bot, setBot] = useState(bot_);
  const {botSave, botDelete, loading, clearError} = BotApi();
  const navigate = useNavigate();

    const editBotHandler = useCallback(async (event) => {
        event.preventDefault();
        try {
            await botSave(bot);
            message.success('Бот сохранен', [5]);
        } catch (e) {
            message.error(e.message || 'Что-то пошло не так', [5]);
            clearError();
        }
    }, [bot]);

    const deleteBotHandler = useCallback(async (event) => {
        event.preventDefault();
        try {
            await botDelete(bot.id);
            message.success('Бот удален!', [5]);
            navigate("/bots");
        } catch (e) {
            message.error(e.message || 'Что-то пошло не так', [5]);
            clearError();
        }
    }, [bot]);

    const handleChange = async (event) => {
        if (event.target.name === "is_active") {
            setBot({...bot, [event.target.name]: event.target.value === "true" ? true : false});
        } else {
            setBot({...bot, [event.target.name]: event.target.value});
        }
    };

  return (
      <div className="row">
          <div className="col s12">
              <div className="card grey lighten-4">
                  <div className="card-content black-text">
                      <span className="card-title" style={{fontWeight: "bold"}}>Бот</span>
                      <form>
                          <form-group>
                              <label className="black-text active" htmlFor="bot_name">Имя</label>
                              <input type="text" name="bot_name" id="bot_name" value={bot.bot_name || ''}
                                     autoComplete="bot_name" onChange={handleChange}/>
                          </form-group>
                          <form-group>
                              <label className="black-text active" htmlFor="bot_token">Токен</label>
                              <input type="text" name="bot_token" id="bot_token" value={bot.bot_token || ''}
                                     autoComplete="bot_token" onChange={handleChange}/>
                          </form-group>
                          <form-group>
                              <label className="black-text active" htmlFor="is_active">Запущен?</label>
                              <select name="is_active" id="is_active" value={bot.is_active ? 'true' : 'false'} onChange={handleChange} style={{display: "block"}}>
                                  <option value="true">Да</option>
                                  <option value="false">Нет</option>
                              </select>
                          </form-group>
                            <br/>
                          <form-group>
                              <button className="btn green darken-2" onClick={editBotHandler} disabled={loading}>Сохранить</button>
                              {" "}
                              <Link to={"/bots/" + bot.id + "/stages"}>
                                  <button className="waves-effect waves-light btn">Схема</button>
                              </Link>
                              {" "}
                              <Link to="/bots">
                                  <button className="btn red darken-3" onClick={deleteBotHandler} disabled={loading}>Удалить</button>
                              </Link>
                              {" "}
                              <Link to="/bots">
                                  <button className="btn grey darken-3">Отмена</button>
                              </Link>
                          </form-group>
                      </form>
                  </div>
              </div>
          </div>
      </div>
  );
}

BotDetail.propTypes = {
    bot_: PropTypes.shape({
        bot_name: PropTypes.string,
        bot_token: PropTypes.string,
        is_active: PropTypes.bool,
        id: PropTypes.number.isRequired
    }),
    bot: PropTypes.shape({
        bot_name: PropTypes.string,
        bot_token: PropTypes.string,
        is_active: PropTypes.bool,
        id: PropTypes.number.isRequired
    })
}