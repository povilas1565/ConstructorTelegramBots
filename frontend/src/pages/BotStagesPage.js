import React, {useCallback, useEffect, useState} from "react";
import {
    DiagramModel,
    default as SRDDefault,
} from '@projectstorm/react-diagrams';
import {BotStages} from "../components/BotStages";
import {StageApi} from "../api/StageApi";
import {useParams} from "react-router-dom";
import {Loader} from "../components/Loader";
import {TelegramKeyboardApi} from "../api/TelegramKeyboardApi";


export const BotStagesPage = () => {
    const [diagramEngine, setDiagramEngine] = useState(null);
    const [stages, setStages] = useState(null);
    const [types, setTypes] = useState(null);
    const {saveStage, getStage, loading, clearError} = StageApi();
    const {getKeyboardType} = TelegramKeyboardApi();
    const botId = useParams().id;


    const getStages = useCallback(async () => {
        const eng = SRDDefault()
        eng.setModel(new DiagramModel());

        const stages = await getStage(botId);
        const types = await getKeyboardType();
        const model = eng.getModel();
        if (stages.telegram_stages.length > 0 && stages.front_options) {
            model.deserializeModel(JSON.parse(stages.front_options), eng);
        }

        setDiagramEngine(eng);
        setStages(stages);
        setTypes(types);
    }, [botId, diagramEngine]);

    useEffect(() => {
        getStages();
    }, []);

    if (loading) {
        return <Loader/>;
    }

    return (
        <>
            { !loading && diagramEngine && <BotStages
                diagram={diagramEngine}
                stages={stages}
                types={types}
                saveStage={saveStage}
                clearError={clearError}
                botId={botId}/> }
        </>
    );
}