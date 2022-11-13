import React, {useCallback} from "react";
import {useMethod} from "../hooks/httpMethods.hook";
import {useHttp} from "../hooks/http.hook";

export const UserApi = () => {
    const userUrl = "/api/user";
    const passwordChangeUrl = "/changePassword"
    const {httpGet, httpPost} = useMethod();
    const {loading, request, error, clearError} = useHttp();

    const userApi = useCallback(async () => {
        return await request(userUrl, httpGet);
    }, []);

    const saveUserApi = useCallback(async (user) => {
        const data = JSON.stringify(user);
        return await request(userUrl, httpPost, data);
    }, []);

    const changePasswordApi = useCallback(async (user) => {
        const data = JSON.stringify(user);
        return await request(userUrl + passwordChangeUrl, httpPost, data);
    }, []);

    return {userApi, saveUserApi, changePasswordApi, loading, error, clearError};
}