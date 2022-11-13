import React, {useEffect, useState} from "react";

export const AuthPage = () => {
    const [form, setForm] = useState({j_username: '', j_password: ''});
    const params = new URLSearchParams(window.location.search)

    useEffect(() => {
        window.M.updateTextFields();
    }, [])

    const changeHandler = event => {
        console.log(params);
        console.log(params.get("error"));
        setForm({ ...form, [event.target.name]: event.target.value})
    }

    const onSubmit = event => {
        event.preventDefault();
    }

    return (
        <div className="row">
            <div className="col s6 offset-s3">
                <div className="card blue darken-1">
                    <div className="card-content white-text">
                        <span className="card-title">Авторизация</span>
                        <form method="POST">
                            <div>
                                <div className="input-field">
                                    <input
                                        placeholder="Введите логин"
                                        id="j_username"
                                        type="text"
                                        name="j_username"
                                        className="yellow-input"
                                        value={form.login}
                                        onChange={changeHandler}
                                    />
                                    <label htmlFor="login">Login</label>
                                </div>

                                <div className="input-field">
                                    <input
                                        placeholder="Введите пароль"
                                        id="j_password"
                                        type="password"
                                        name="j_password"
                                        className="yellow-input"
                                        value={form.password}
                                        onChange={changeHandler}
                                    />
                                    <label htmlFor="password">Password</label>
                                </div>

                            </div>
                                <div className="card-action">
                                    <button className="btn yellow darken-4" type='submit' onSubmit={onSubmit}>Login</button>
                                </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}