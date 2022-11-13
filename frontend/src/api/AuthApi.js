import {useCallback} from "react";
import {useHttp} from "../hooks/http.hook";
import {useMethod} from "../hooks/httpMethods.hook";


export const AuthApi = () => {
    const LOGOUT_URL = '/api/logout';
    const {httpPost} = useMethod();
    const {loading, request, error, clearError} = useHttp();

    const logout = useCallback(async () => {
        await request(LOGOUT_URL, httpPost);
    }, []);

    return {logout, error, loading, clearError};
}