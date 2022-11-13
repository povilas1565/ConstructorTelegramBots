import React, {useCallback} from "react";
import {useMethod} from "../hooks/httpMethods.hook";
import {useHttp} from "../hooks/http.hook";

export const TelegramKeyboardApi = () => {
    const keyboardTypeUrl = '/api/telegram/keyboard/type';
    const {httpGet, httpPost} = useMethod();
    const {loading, request, error, clearError} = useHttp();

    const getKeyboardType = useCallback(async () => {
        return await request(keyboardTypeUrl, httpGet);
    }, []);

    return {getKeyboardType, loading, error, clearError};
}