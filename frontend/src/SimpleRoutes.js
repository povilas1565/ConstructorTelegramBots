import React from 'react';
import { Route, Routes, Navigate } from "react-router-dom";
import {AuthPage} from "./pages/AuthPage";
import {BotsPage} from "./pages/BotsPage";
import {BotStagesPage} from "./pages/BotStagesPage";
import {ProfilePage} from "./pages/ProfilePage";
import {BotDetailPage} from "./pages/BotDetailPage";
import {RegisterPage} from "./pages/RegisterPage";
import {NewBotsPage} from "./pages/NewBotsPage";

export const SimpleRoutes = isAuth => {
    if (isAuth) {
        return (
            <Routes>
                <Route path='/profile' element={<ProfilePage/>} />
                <Route path='/bots' element={<BotsPage/>} />
                <Route path='/bots/new' element={<NewBotsPage/>} />
                <Route path='/bots/:id' element={<BotDetailPage/>} />
                <Route path='/bots/:id/stages' element={<BotStagesPage/>} />
                <Route path='/*' element={<Navigate replace to='/bots' />} />
            </Routes>
        )
    }

    return (
        <Routes>
            <Route path='/auth/login' exact={true} element={<AuthPage/>} />
            <Route path='/auth/register' exact={true} element={<RegisterPage/>} />
            <Route path='/*' element={<Navigate replace to='/auth/login' />} />
        </Routes>
    );
}