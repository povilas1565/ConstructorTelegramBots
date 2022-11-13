import React, {useState, useCallback, useContext} from "react";
import {AuthContext} from "../context/AuthContext";
import {useNavigate} from "react-router-dom";

export const useHttp = () => {
    const auth = useContext(AuthContext);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate()

    const request = useCallback(async (url, method = 'GET', body = null, headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Cookie': null
    }) => {
        setLoading(true)

        if (auth.isAuth) {
            headers.Cookie = `JSESSIONID=${auth.token}`;
        }

        try {
            const response = await fetch(url, {method, body, headers});
            if (response.redirected) {
                window.location.href = response.url;
                return ;
            }
            let data = null;
            try {
                data = await response.json();
            } catch (e) {
                //pass
            }

            if (!response.ok) {
                throw new Error(data.message || 'Что-то пошло не так');
            }

            return data ? data : true;
        } catch (e) {
            setError(e.message);
            throw e;
        } finally {
            setLoading(false);
        }
    }, []);

    const clearError = useCallback(() => setError(null), []);

    return { loading, request, error, clearError }
}