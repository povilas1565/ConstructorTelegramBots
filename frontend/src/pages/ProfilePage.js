import React, {useCallback, useEffect} from "react";
import {UserApi} from "../api/UserApi";
import {Loader} from "../components/Loader";
import {User} from "../components/User";

export const ProfilePage = () => {
    const [user, setUser] = React.useState(null);
    const {userApi, loading} = UserApi();

    const getUser = useCallback(async () => {
        const user = await userApi();
        setUser(user);
    }, [userApi]);

    useEffect(() => {
        getUser();
    }, [getUser]);

    if (loading) {
        return <Loader/>;
    }

    return (
        <>
            { !loading && user && <User user_={user}/> }
        </>
    )
}