import React, {useCallback} from "react";
import {useMethod} from "../hooks/httpMethods.hook";
import {useHttp} from "../hooks/http.hook";

export const StageApi = () => {
    const stageSaveUrl = '/api/telegram/bot';
    const stage = '/api/telegram/bot';
    const {httpGet, httpPost} = useMethod();
    const {loading, request, error, clearError} = useHttp();

    const saveStage = useCallback(async (stages, botId) => {
        const data = JSON.stringify(stages);
        return await request(`${stageSaveUrl}/${botId}/stages`, httpPost, data);
    }, []);

    const getStage = useCallback(async (botId) => {
        return await request(`${stage}/${botId}/stages`, httpGet);
    }, []);

    return {saveStage, getStage, loading, error, clearError};
}