import React, {useState} from "react";
import {Button, Col, List, Modal, Row} from "antd";
import {ModalProps} from "antd/es/modal/interface";

import './pluginTool.css'
import {useGet, useGetManual} from "../../../hooks/useApis.ts";

export type PluginToolsProps = {
    onSelectedItem?: (item: any) => void
} & ModalProps
export const PluginTools: React.FC<PluginToolsProps> = (props) => {

    const [selectedIndex, setSelectedIndex] = useState(-1);
    const {result: data} = useGet('/api/v1/aiPlugin/list')
    const {loading, result: toolsData, doGet: getTools} = useGetManual('/api/v1/aiPluginTool/list')

    return (
        <Modal title={'选择插件'} footer={null} {...props} width={"800px"}
               height={"600px"}>
            <Row gutter={16} style={{width: "100%"}}>
                <Col span={6} style={{borderRight: "1px solid #e2e2e2"}}>
                    {data?.data.map((item: any, index: number) => {
                        return (
                            <div key={index} className={`main-item ${index == selectedIndex ? 'active' : ''}`}
                                 onClick={() => {
                                     getTools({
                                         params: {
                                             pluginId: item.id
                                         }
                                     })
                                     setSelectedIndex(index)
                                 }}>
                                {item.name}
                            </div>)
                    })
                    }
                </Col>
                <Col span={18}>
                    <List
                        loading={loading}
                        dataSource={toolsData?.data}
                        renderItem={(item: any, index) => (
                            <List.Item
                                key={index}
                                actions={[<Button onClick={() => {
                                    props.onSelectedItem?.(item)
                                }}>选择</Button>]}
                            >
                                <List.Item.Meta
                                    title={item.name}
                                    description={item.description}
                                />
                            </List.Item>
                        )}
                    />
                </Col>
            </Row>
        </Modal>
    )
}