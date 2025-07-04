// @ts-ignore
import React, {useRef, useState} from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm";
import {CaretRightOutlined, CloseCircleOutlined, CodeOutlined, EditOutlined} from "@ant-design/icons";
import {useGetManual} from "../../hooks/useApis";
import {Button, message} from "antd";
import {SysJobModal} from './SysJobModal'
import {useNavigate} from "react-router-dom";
import {useCheckPermission} from "../../hooks/usePermissions.tsx";

export const SysJob: React.FC = () => {

    const navigate = useNavigate();
    const pageRef = useRef<any>(null);
    const {doGet: startJob} = useGetManual('/api/v1/sysJob/start')
    const {doGet: stopJob} = useGetManual('/api/v1/sysJob/stop')
    //字段配置
    const columnsConfig: ColumnsConfig<any> = [
        {
            hidden: true,
            form: {
                type: "hidden"
            },
            dataIndex: "id",
            key: "id"
        },

        {
            supportSearch: true,
            form: {
                type: "input",
                rules: [{required: true}]
            },
            dataIndex: "jobName",
            title: "任务名称",
            key: "jobName"
        },

        {
            form: {
                type: "select",
                rules: [{required: true, message: "请选择任务类型"}]
            },
            dataIndex: "jobType",
            title: "任务类型",
            key: "jobType",
            dict: {
                name: "jobType"
            }
        },

        {
            form: {
                type: "input",
                rules: [{required: true, message: "请输入cron表达式"}]
            },
            dataIndex: "cronExpression",
            title: "cron表达式",
            key: "cronExpression"
        },

        {
            form: {
                type: "hidden"
            },
            dataIndex: "status",
            title: "任务状态",
            key: "status",
            dict: {
                name: "jobStatus"
            }
        },

        {
            form: {
                type: "hidden"
            },
            dataIndex: "allowConcurrent",
            title: "并发执行",
            key: "allowConcurrent",
            dict: {
                name: "yesOrNo"
            }
        },

        {
            form: {
                type: "hidden"
            },
            dataIndex: "misfirePolicy",
            title: "错过策略",
            key: "misfirePolicy",
            dict: {
                name: "misfirePolicy"
            }
        },

        {
            form: {
                type: "hidden"
            },
            dataIndex: "created",
            title: "创建时间",
            key: "created"
        },

        {
            form: {
                type: "input"
            },
            dataIndex: "remark",
            title: "备注",
            key: "remark"
        },
    ];
    const jobModaRef = useRef<any>(null);
    //编辑页面设置
    const editLayout = {
        labelLayout: "horizontal",
        labelWidth: 80,
        columnsCount: 1,
        openType: "modal",
    } as EditLayout;

    const canSave = useCheckPermission("/api/v1/sysJob/save")
    const canQuery = useCheckPermission("/api/v1/sysJob/query")

    const [modalOpen, setModalOpen] = useState(false)
    const getActions = (data: any) => {
        return (
            <>
                {canSave && data.status === 0 &&
                    <a onClick={() => {
                        startJob({
                            params: {
                                id: data.id
                            }
                        }).then(res => {
                            if (res.data.errorCode === 0) {
                                message.success("成功");
                            }
                            if (pageRef.current) {
                                pageRef.current.refresh();
                            }
                        })
                    }}> <CaretRightOutlined/> 启动 </a>
                }

                {canSave && data.status === 1 && <a onClick={() => {
                    stopJob({
                        params: {
                            id: data.id
                        }
                    }).then(res => {
                        if (res.data.errorCode === 0) {
                            message.success("成功");
                        }
                        if (pageRef.current) {
                            pageRef.current.refresh();
                        }
                    })
                }}> <CloseCircleOutlined/> 停止 </a>
                }

                {canQuery && <a onClick={() => {
                    navigate('/sys/sysJobLog', {
                        state: {
                            jobId: data.id,
                        }
                    })
                }}> <CodeOutlined/> 日志 </a>}

                {canSave && data.status === 0 &&
                    <a onClick={() => {
                        jobModaRef.current.setData(data)
                        setModalOpen(true)
                    }}> <EditOutlined/> 编辑 </a>
                }

            </>
        )
    }

    //操作列配置
    const actionConfig = {
        addButtonEnable: false,
        detailButtonEnable: false,
        deleteButtonEnable: true,
        editButtonEnable: false,
        hidden: false,
        width: "260px",
        customActions: (data) => {
            return (
                <>
                    {getActions(data)}
                </>
            )
        }

    } as ActionConfig<any>

    const customBtn = () => {
        return (canSave && <Button type="primary" onClick={() => {
                setModalOpen(true)
                jobModaRef.current.setData(null)
            }}>新增</Button>
        )
    }

    const handleOk = () => {
        setModalOpen(false);
        if (pageRef.current) {
            pageRef.current.refresh();
        }
    };

    const handelCancel = () => {
        setModalOpen(false)
    }

    return (
        <>
            <SysJobModal
                ref={jobModaRef}
                open={modalOpen}
                onModalOk={handleOk}
                onModalCancel={handelCancel}
            />
            <CrudPage
                ref={pageRef}
                addButtonEnable={false}
                customButton={customBtn}
                columnsConfig={columnsConfig} tableAlias="sysJob"
                actionConfig={actionConfig} editLayout={editLayout}/>
        </>
    )
};

export default {
    path: "/sys/sysJob",
    element: SysJob
};
