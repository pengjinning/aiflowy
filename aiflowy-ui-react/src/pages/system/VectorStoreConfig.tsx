import React from 'react';
import {Alert, Form, Input, Select} from "antd";
import FormTitle from "../../components/FormTitle";
import OptionsPage from "../../components/OptionsPage";

const VectorStoreConfig: React.FC = () => {

    return (
        <OptionsPage>

            <FormTitle style={{marginTop: "0"}}>向量数据库</FormTitle>

            <Form.Item label="数据库类型" name="vectorstore_type" initialValue={"aliyun"}>
                <Select>
                    <Select.Option value="milvus">Milvus</Select.Option>
                    <Select.Option value="aliyun">阿里云</Select.Option>
                    <Select.Option value="qcloud">腾讯云</Select.Option>
                </Select>
            </Form.Item>


            <Form.Item label="默认集合名称" name="vectorstore_default_collection">
                <Input placeholder="请输入默认的集合名称"/>
            </Form.Item>

            <FormTitle>Milvus 向量数据库配置</FormTitle>

            <Form.Item label="Endpoint" name="milvus_endpoint" extra={"请输入 Milvus 的服务地址，例如：http://localhost:19530"}>
                <Input placeholder="输入 Milvus 的服务地址"/>
            </Form.Item>

            <Form.Item label="ApiKey" name="milvus_apikey" extra={<>Milvus 的 Api Key 请去或 Token</>}>
                <Input placeholder="请输入Milvus 的 Api Key"/>
            </Form.Item>

            <Form.Item label="数据库名称" name="milvus_database" extra={"数据库的名称，也是 Cluster 的名称。"}>
                <Input placeholder="请输入数据库名称"/>
            </Form.Item>


            <FormTitle>阿里云向量数据库配置</FormTitle>

            <Form.Item label="Endpoint" name="aliyun_vdb_endpoint" extra={<>获取 Endpoint 请先创建 Cluster，详情
                <a href='https://help.aliyun.com/document_detail/2631966.html'
                   target='_blank'>https://help.aliyun.com/document_detail/2631966.html</a>。</>}>
                <Input placeholder="请输入阿里云向量数据库的 Endpoint"/>
            </Form.Item>

            <Form.Item label="ApiKey" name="aliyun_vdb_api_key" extra={<>阿里云的 Api Key 请去
                <a href='https://help.aliyun.com/document_detail/2510230.html'
                   target='_blank'>https://help.aliyun.com/document_detail/2510230.html</a> 获取。</>}>
                <Input placeholder="请输入阿里云向量数据库的 Api Key"/>
            </Form.Item>

            <Form.Item label="数据库名称" name="aliyun_vdb_database" extra={"数据库的名称，也是 Cluster 的名称。"}>
                <Input placeholder="请输入数据库名称"/>
            </Form.Item>


            <FormTitle>腾讯云向量数据库配置</FormTitle>

            <Form.Item wrapperCol={{span: 12, offset: 4}}>
                <Alert
                    message={<>腾讯云向量数据库配置的信息获取，请参考：<a
                        href='https://cloud.tencent.com/document/product/1709/97769'
                        target='_blank'>https://cloud.tencent.com/document/product/1709/97769</a>
                    </>}
                    type="info"/>
            </Form.Item>

            <Form.Item label="Endpoint" name="qcloud_vdb_endpoint">
                <Input placeholder="请输入腾讯云向量数据库的 Endpoint"/>
            </Form.Item>

            <Form.Item label="Username" name="qcloud_vdb_username">
                <Input placeholder="请输入腾讯云向量数据库的 Api Key"/>
            </Form.Item>

            <Form.Item label="ApiKey" name="qcloud_vdb_api_key">
                <Input placeholder="请输入腾讯云向量数据库的 Api Key"/>
            </Form.Item>

            <Form.Item label="数据库名称" name="qcloud_vdb_database">
                <Input placeholder="请输入数据库名称"/>
            </Form.Item>

        </OptionsPage>
    );
};

export default {
    path: "config/vector",
    element: VectorStoreConfig
};
