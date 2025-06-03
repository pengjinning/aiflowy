import React from "react";
import {BotModal, BotModalProps} from "./BotModal.tsx";

export type KnowledgeModalProps =
    Partial<Pick<BotModalProps, 'tableAlias' | 'tableTitle'>> &
    Omit<BotModalProps, 'tableAlias' | 'tableTitle'>;
export const KnowledgeModal: React.FC<KnowledgeModalProps> = ({ tableAlias = "aiKnowledge", tableTitle = "知识库", ...props }) => {
    return (
        <BotModal tableAlias={tableAlias} tableTitle={tableTitle} {...props} />
    );
};