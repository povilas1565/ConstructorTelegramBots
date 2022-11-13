import React, {useCallback, useState} from "react";
import PropTypes from "prop-types";
import {UserApi} from "../api/UserApi";
import {message} from "antd";

export const User = ({user_}) => {
    const {userApi, saveUserApi, changePasswordApi, loading, clearError} = UserApi();
    const [user, setUser] = useState({...user_})

    const editUserHandler = useCallback(async (event) => {
        event.preventDefault();
        try {
            await saveUserApi(user);
            message.success('Пользователь сохранен!', [5]);
        } catch (e) {
            message.error(e.message || 'Что-то пошло не так', [5]);
            clearError();
        }
    }, [user]);

    const changePasswordHandler = useCallback(async (event) => {
        event.preventDefault();
        try {
            await changePasswordApi(user);
            message.success('Пароль изменен', [5]);
        } catch (e) {
            message.error(e.message || 'Что-то пошло не так', [5]);
            clearError();
        }
    }, [user]);

    const handleChange = async (event) => {
        setUser({...user, [event.target.name]: event.target.value})
    }

    return (
        <div className="row">
            <div className="col s12">
                <div className="card grey lighten-4">
                    <div className="card-content black-text">
                        <span className="card-title" style={{fontWeight: "bold"}}>Профиль</span>
                        <form>
                            <form-group>
                                <label className="black-text active" htmlFor="name">Имя</label>
                                <input type="text" name="name" id="name" value={user.name || ''}
                                       autoComplete="name" onChange={handleChange}/>
                            </form-group>
                            <form-group>
                                <label className="black-text active" htmlFor="username">Имя пользователя</label>
                                <input type="text" name="username" id="username" value={user.username || ''}
                                       readOnly/>
                            </form-group>
                            <form-group>
                                <label className="black-text active" htmlFor="isLocked">Блокировка пользователя</label>
                                <input type="text" name="isLocked" id="isLocked" value={user.isLocked ? 'Да' : 'Нет'}
                                       readOnly/>
                            </form-group>
                            <form-group>
                                <button className="btn green darken-2" onClick={editUserHandler} disabled={loading}>Сохранить</button>
                            </form-group>
                        </form>
                    </div>
                </div>
            </div>
            <div className="col s12">
                <div className="card grey lighten-4">
                    <div className="card-content black-text">
                        <span className="card-title" style={{fontWeight: "bold"}}>Сменить пароль</span>
                        <form>
                            <form-group>
                                <label className="black-text active" htmlFor="name">Новый пароль</label>
                                <input type="password" name="password" id="password" value={user.password || ''}
                                       autoComplete="name" onChange={handleChange}/>
                            </form-group>
                            <form-group>
                                <button className="btn green darken-2" onClick={changePasswordHandler} disabled={loading}>Сменить пароль</button>
                            </form-group>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

User.propTypes = {
  user_: PropTypes.shape({
      id: PropTypes.number.isRequired,
      name: PropTypes.string,
      username: PropTypes.string.isRequired,
      password: PropTypes.string,
      isLocked: PropTypes.bool,
  })
}