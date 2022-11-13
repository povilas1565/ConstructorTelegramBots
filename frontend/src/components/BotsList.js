import React from "react";
import {Link} from "react-router-dom";
import PropTypes from "prop-types";

export const BotsList = ({listBots}) => {
    let bots;

    bots = listBots.map(bot => {
        let status;

        if (bot.is_active) {
            status = "Active";
        } else {
            status = "In Active";
        }

        return <tr key={bot.id}>
            <td>{bot.bot_name}</td>
            <td>{bot.bot_token.substr(0, 15) + "****"}</td>
            <td>{status}</td>
            <td>
                <Link to={"/bots/" + bot.id}>
                    <button className="waves-effect waves-light btn">Редактировать</button>
                </Link>
                {' '}
                <Link to={"/bots/" + bot.id + "/stages"}>
                    <button className="waves-effect waves-light btn">Схема</button>
                </Link>
            </td>
        </tr>
    });

    return (
        <div className="row">
            <div className="col s12">
                <div className="card grey lighten-4">
                    <div className="card-content black-text">
                        <span className="card-title" style={{fontWeight: "bold"}}>Мои боты
                            {' '}
                            <Link to={"/bots/new"}>
                                <button className="waves-effect waves-light btn">Новый бот</button>
                            </Link>
                        </span>
                        <table className="highlight">
                            <thead>
                            <tr>
                                <th width="30%">Наименование</th>
                                <th width="30%">Токен</th>
                                <th width="10%">Статус</th>
                                <th width="40%">Кнопки</th>
                            </tr>
                            </thead>
                            <tbody>
                            {bots}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    )
}

BotsList.propTypes = {
    listBots: PropTypes.arrayOf(PropTypes.shape({
        bot_name: PropTypes.string,
        bot_token: PropTypes.string,
        is_active: PropTypes.bool,
        id: PropTypes.number.isRequired
    }))
}