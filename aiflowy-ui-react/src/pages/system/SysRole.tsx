import React, {useEffect, useState} from 'react';
import {ActionConfig, ColumnsConfig} from "../../components/AntdCrud";
import CrudPage from "../../components/CrudPage";
import {EditLayout} from "../../components/AntdCrud/EditForm.tsx";
import {SettingOutlined} from "@ant-design/icons";
import {Modal, Spin, Tree, TreeProps} from "antd";
import {Key} from "rc-table/lib/interface";
import {useGetManual, usePostManual} from "../../hooks/useApis.ts";
import {useCheckPermission} from "../../hooks/usePermissions.tsx";

//编辑页面设置
const editLayout = {
    labelLayout: "horizontal",
    labelWidth: 80,
    columnsCount: 1,
    openType: "modal"
} as EditLayout;

export const SysRole: React.FC = () => {

    const [open, setOpen] = useState(false);
    const [roleId, setRoleId] = useState(null);
    const {result: treeData, doGet: getTreeData} = useGetManual("/api/v1/sysMenu/tree",);
    const {doGet: getCheckedKeys} = useGetManual(`/api/v1/sysMenu/getCheckedByRoleId/${roleId}`,);
    const [checkedKeys, setCheckedKeys] = useState<Key[]>([]);
    const {loading: saveLoading, doPost: saveRoleMenu} = usePostManual(`/api/v1/sysRole/saveRoleMenu/${roleId}`);

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
            form: {
                type: "input",
                rules: [{required: true}],
            },
            dataIndex: "roleName",
            title: "角色名称",
            key: "roleName",
            supportSearch: true,
        },

        {
            form: {
                type: "input",
                rules: [{required: true}],
            },
            dataIndex: "roleKey",
            title: "角色标识",
            key: "roleKey",
            editCondition: (data) => {
                return !isAdminRole(data);
            },
        },

        {
            form: {
                type: "select"
            },
            dataIndex: "status",
            title: "数据状态",
            key: "status",
            dict: {
                name: "dataStatus"
            }
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

    const canSave = useCheckPermission("/api/v1/sysRole/save")

    //操作列配置
    const actionConfig = {
        addButtonEnable: true,
        detailButtonEnable: false,
        deleteButtonEnable: true,
        editButtonEnable: true,
        hidden: false,
        width: "250px",
        customActions: (data) => {
            if (isAdminRole(data)) {
                return <></>
            }
            return (
                canSave && !(data.id == 1) && <a onClick={() => {
                    setOpen(true)
                    setRoleId(data.id)
                }}> <SettingOutlined/> 权限分配</a>
            )
        },

    } as ActionConfig<any>

    const isAdminRole = (data: any) => {
        return (data?.roleKey == "tenant_admin" || data?.roleKey == "super_admin")
    }

    const [loadingData, setLoadingData] = useState(false);

    useEffect(() => {
        if (open) {
            setLoadingData(true)
            getTreeData()
                .then(getCheckedKeys)
                .then((resp) => {
                    setLoadingData(false)
                    setCheckedKeys(resp.data?.data)
                })
        }
    }, [open])

    const onClose = () => {
        setOpen(false);
    };

    const onCheck: TreeProps['onCheck'] = (checkedKeys) => {
        // setCheckedKeys(checkedKeys as Key[])
        if (typeof checkedKeys === "object" && checkedKeys !== null && "checked" in checkedKeys) {
            setCheckedKeys(checkedKeys.checked as Key[]);
        } else {
            setCheckedKeys(checkedKeys as Key[]);
        }
    };

    return (
        <>
            <Modal title="权限分配"
                   open={open}
                   width={"600px"}
                   onCancel={onClose}
                   onOk={() => {
                       saveRoleMenu({
                           data: checkedKeys
                       }).then(() => {
                           setOpen(false)
                       })
                   }}
                   confirmLoading={saveLoading}
                   destroyOnClose
            >
                <Spin spinning={loadingData}>
                    <Tree treeData={treeData?.data}
                          style={{height: "400px", overflowY: "auto"}}
                          defaultExpandParent
                          checkStrictly
                          checkable
                          checkedKeys={checkedKeys}
                          onCheck={onCheck}
                          fieldNames={{
                              key: "id",
                              title: "menuTitle",
                              children: "children"
                          }}
                    />
                </Spin>
            </Modal>
            <CrudPage columnsConfig={columnsConfig} tableAlias="sysRole"
                      actionConfig={actionConfig} editLayout={editLayout}/>
        </>

    )
};

export default {
    path: "/sys/sysRole",
    element: SysRole
};
