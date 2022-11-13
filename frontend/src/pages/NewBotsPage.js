import React, {useCallback, useState} from "react";
import {Link, useNavigate } from "react-router-dom";
import {BotApi} from "../api/BotApi";
import {message} from "antd";

export const NewBotsPage = () => {
    const [bot, setBot] = useState({id: null, bot_name: null, bot_token: null});
    const {botSave, loading, clearError} = BotApi();
    const navigate = useNavigate();

    const editBotHandler = useCallback(async (event) => {
        event.preventDefault();
        try {
            const data = await botSave(bot);
            message.success('Бот сохранен', [5]);
            navigate("/bots/" + data.id);
        } catch (e) {
            message.error(e.message || 'Что-то пошло не так', [5]);
            clearError();
        }
    }, [bot]);

    const handleChange = async (event) => {
        setBot({...bot, [event.target.name]: event.target.value})
    }

    return (
        <div className="row">
            <div className="col s12">
                <div className="card grey lighten-4">
                    <div className="card-content black-text">
                        <span className="card-title" style={{fontWeight: "bold"}}>Бот</span>
                        <form>
                            <form-group>
                                <label className="black-text active" htmlFor="bot_name">Имя</label>
                                <input type="text" name="bot_name" id="bot_name" value={bot ? bot.bot_name || '' : ''}
                                       autoComplete="bot_name" onChange={handleChange}/>
                            </form-group>
                            <form-group>
                                <label className="black-text active" htmlFor="bot_token">Токен</label>
                                <input type="text" name="bot_token" id="bot_token" value={bot ? bot.bot_token || '' : ''}
                                       autoComplete="bot_token" onChange={handleChange}/>
                            </form-group>
                            <form-group>
                                <button className="btn green darken-2" onClick={editBotHandler} disabled={loading}>Сохранить</button>
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