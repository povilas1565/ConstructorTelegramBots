import React from "react";
import { BrowserRouter } from "react-router-dom";
import {SimpleRoutes} from "./SimpleRoutes";
import 'materialize-css';
import 'antd/dist/antd.css';
import {NavBar} from "./components/NavBar";

function App() {
  //TODO
  const isAuth = true;
  const routes = SimpleRoutes(isAuth);

  return (
    <BrowserRouter>
      { isAuth && <NavBar/> }
      {routes}
    </BrowserRouter>
  );
}

export default App;
