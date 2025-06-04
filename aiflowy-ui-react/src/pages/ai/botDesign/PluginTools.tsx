import React, {useState} from "react";
import {Button, Col, Empty, List, Modal, Pagination, Row, Spin} from "antd";
import {ModalProps} from "antd/es/modal/interface";

import './pluginTool.css'
import {useGet} from "../../../hooks/useApis.ts";
import Search from "antd/es/input/Search";
import {useNavigate} from "react-router-dom";

import {AccordionItem} from '../../../components/Accordion/Accordion.tsx';
import '../../../components/Accordion/accordion.css'

export type PluginToolsProps = {
    selectedItem?: any[],
    goToPage: string,
    onSelectedItem?: (item: any) => void,
    onRemoveItem?: (item: any) => void,
    onClose?: () => void
} & ModalProps
export const PluginTools: React.FC<PluginToolsProps> = (props) => {

    const selected = props.selectedItem;
    const [pageParams, setPageParams] = useState({
        pageNumber: 1,
        pageSize: 10,
        name: ''
    });
    const [selectedIndex, setSelectedIndex] = useState(-1);

    const {loading: pageDataLoading, result: pageData, doGet: getPageData} = useGet('/api/v1/aiPlugin/page', {
        pageNumber: pageParams.pageNumber,
        pageSize: pageParams.pageSize
    })

    const navigate = useNavigate();
    const goToPage = props.goToPage;

    const onSearch = (value: string) => {
        setPageParams({
            ...pageParams,
            name: value
        })
        getPageData({
            params: {
                pageNumber: 1,
                pageSize: pageParams.pageSize,
                name: value
            }
        })
    };

    const changeCurrent = (page: number) => {
        setPageParams({
            ...pageParams,
            pageNumber: page,
            pageSize: pageParams.pageSize
        })
        getPageData({
            params: {
                pageNumber: page,
                pageSize: pageParams.pageSize
            }
        })
    }

    return (
        <Modal title={'选择插件'} footer={null} {...props} width={"1000px"}
               height={"600px"}>
            <Row gutter={16} style={{width: "100%"}}>
                <Col span={6} style={{backgroundColor: "#f5f5f5", paddingTop: "10px"}}>
                    <Search style={{marginBottom: "10px"}} placeholder="搜索" onSearch={onSearch}/>
                    <Button block type={"primary"} onClick={() => {
                        goToPage ? navigate(goToPage) : ''
                    }}>创建插件</Button>
                </Col>
                <Col span={18}>
                    <Spin spinning={pageDataLoading}>
                        <div>
                            {pageData?.data.totalRow > 0 ? pageData?.data.records.map((item: any, index: number) => {
                                return (
                                    <AccordionItem
                                        key={index}
                                        icon={item.icon}
                                        title={item.name}
                                        description={item.description}
                                        isActive={selectedIndex == index}
                                        clickItem={() => {
                                            if (selectedIndex == index) {
                                                setSelectedIndex(-1)
                                            } else {
                                                setSelectedIndex(index)
                                            }
                                        }}
                                    >
                                        <List
                                            dataSource={item.tools}
                                            renderItem={(item: any, index) => (
                                                <List.Item
                                                    key={index}
                                                    actions={selected?.includes(item.id)?[<Button color="danger" variant="outlined" onClick={() => {
                                                        props.onRemoveItem?.(item)}
                                                    }>
                                                        移除
                                                    </Button>]:[<Button onClick={() => {
                                                        props.onSelectedItem?.(item)}
                                                    }>
                                                        选择
                                                    </Button>]}
                                                >
                                                    <List.Item.Meta
                                                        title={item.name}
                                                        description={item.description}
                                                    />
                                                </List.Item>
                                            )}
                                        />
                                    </AccordionItem>
                                )
                            }) : <Empty style={{height: "100%"}} image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                            }
                            <div style={{padding: "10px", backgroundColor: "#f5f5f5"}}>
                                <Pagination onChange={changeCurrent} align="end" defaultCurrent={pageParams.pageNumber}
                                            pageSize={pageParams.pageSize} total={pageData?.data.totalRow}/>
                            </div>
                        </div>
                    </Spin>
                </Col>
            </Row>
        </Modal>
    )
}