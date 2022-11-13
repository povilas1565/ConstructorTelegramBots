import {useMethod} from "../hooks/httpMethods.hook";
import {useHttp} from "../hooks/http.hook";
import {useCallback} from "react";


export const BotApi = () => {
    const botListUrl = "/api/telegram/bot/list";
    const botSaveUrl = "/api/telegram/bot";
    const botDetailUrl = "/api/telegram/bot";
    const botDeleteUrl = "/api/telegram/bot";
    const {httpGet, httpPost, httpDelete} = useMethod();
    const {loading, request, error, clearError} = useHttp();

    const botList = useCallback(async () => {
        return await request(botListUrl, httpGet);
    }, []);

    const botDetail = useCallback(async (id) => {
        return await request(`${botDetailUrl}/${id}`, httpGet);
    }, []);

    const botSave = useCallback(async (bot) => {
        const data = JSON.stringify(bot);
        return await request(botSaveUrl, httpPost, data);
    }, []);

    const botDelete = useCallback(async (id) => {
        await request(`${botDeleteUrl}/${id}`, httpDelete);
    }, []);

    return {botList, botSave, botDetail, botDelete, loading, error, clearError};
}