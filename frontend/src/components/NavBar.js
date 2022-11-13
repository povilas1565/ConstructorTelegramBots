import React from "react";
import {NavLink} from "react-router-dom";
import {AuthApi} from "../api/AuthApi";

export const NavBar = () => {
    const {logout} = AuthApi();

    const logoutHandler = event => {
        event.preventDefault();
        logout();
        window.location.href='/login.html';
    }

    return (
        <nav>
            <div className="nav-wrapper grey darken-3" style={{ padding: '0 2rem' }}>
                <span className="brand-logo">Конструктор</span>
                <ul id="nav-mobile" className="right hide-on-med-and-down">
                    <li><NavLink to="/bots" className="nav-link px-2 text-white">Мои боты</NavLink></li>
                    <li><NavLink to="/profile" className="nav-link px-2 text-white">Профиль</NavLink></li>
                    <li><a href="/" onClick={logoutHandler} className="nav-link px-2 text-white">Выйти</a></li>
                </ul>
            </div>
        </nav>
    )
}