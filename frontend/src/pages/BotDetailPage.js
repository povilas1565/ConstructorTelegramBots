import React, {useCallback, useEffect} from "react";
import {Loader} from "../components/Loader";
import {BotApi} from "../api/BotApi";
import {useParams} from "react-router-dom";
import {BotDetail} from "../components/BotDetail";

export const BotDetailPage = () => {
    const [bot, setBot] = React.useState(null);
    const {botDetail, loading} = BotApi();
    const botId = useParams().id;

    const getBot = useCallback(async () => {
        const bot = await botDetail(botId);
        if (bot.is_active == null) {
            bot.is_active = false;
        }
        setBot(bot);
    }, []);

    useEffect(() => {
        getBot();
    }, [getBot]);

    if (loading) {
        return <Loader/>;
    }

    return (
        <>
            { !loading && bot && <BotDetail bot_={bot}/> }
        </>
    )
}