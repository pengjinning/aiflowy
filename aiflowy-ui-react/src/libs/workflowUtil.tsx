import {Uploader} from "../components/Uploader";
import {Button, Input} from "antd";
import {UploadOutlined} from "@ant-design/icons";

export function DynamicItem(item: any, form: any,  attrName: any): JSX.Element {

    function setNestedField(obj: any, path: string, value: any) {
        const keys = path.split('.');
        let current = obj;

        // 遍历路径（除了最后一个 key）
        for (let i = 0; i < keys.length - 1; i++) {
            const key = keys[i];
            if (!current[key]) {
                current[key] = {}; // 如果中间路径不存在，创建空对象
            }
            current = current[key];
        }

        // 设置最终的值
        current[keys[keys.length - 1]] = value;
        return obj;
    }

    function makeItem(item: any, form: any, attrName: any) {
        let inputComponent;
        switch (item.dataType) {
            case "File":
                inputComponent = (
                    <Uploader
                        maxCount={1}
                        action={'/api/v1/commons/uploadAntd'}
                        children={<Button icon={<UploadOutlined/>}>上传</Button>}
                        onChange={({file}) => {
                            if (file.status === 'done') {
                                let url = file.response?.response.url;
                                if (url.indexOf('http') < 0) {
                                    url = window.location.origin + url;
                                }
                                if (attrName.indexOf(".") > -1) {
                                    const currentValues = form.getFieldsValue();
                                    const updatedValues = setNestedField(currentValues, attrName, url);
                                    form.setFieldsValue(updatedValues);
                                } else {
                                    form.setFieldsValue({
                                        [attrName]: url,
                                    });
                                }

                            }
                        }}
                    />);
                break;
            default:
                inputComponent = <Input/>;
        }
        return inputComponent
    }

    return (
        <>
            {makeItem(item, form, attrName)}
        </>
    )

}