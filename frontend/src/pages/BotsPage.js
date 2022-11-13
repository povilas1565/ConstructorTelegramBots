import React, {useCallback, useEffect, useState} from "react";
import {BotsList} from "../components/BotsList";
import {BotApi} from "../api/BotApi";
import {Loader} from "../components/Loader";

export const BotsPage = () => {
    const {botList, loading} = BotApi();
    const [listBots, setListBots] = useState(null);

    const getBots = useCallback(async () => {
        const bots = await botList();
        setListBots(bots);
    }, [botList]);

    useEffect(() => {
        getBots();
    }, [getBots]);

    if (loading) {
        return <Loader/>;
    }

    return (
        <>
            { !loading && listBots && <BotsList listBots={listBots}/>}
        </>
    )
}