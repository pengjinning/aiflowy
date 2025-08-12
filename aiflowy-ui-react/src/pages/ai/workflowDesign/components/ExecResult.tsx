import React, {useEffect} from "react"
import JsonView from "react18-json-view";
import {Empty, Image} from "antd";
import css from './execresult.module.css'
import confirmOther from "../../../../assets/confirm-other.png";
import confirmFile from "../../../../assets/confirm-file.png";

export type ExecResultProps = {
    nodes: any[],
    result: any
}

export const ExecResult: React.FC<ExecResultProps> = ({nodes = [], result = {}}) => {

    const [finalNode, setFinalNode] = React.useState<any>({})

    const getIcon = (type: string) => {
        return type === 'other'? confirmOther : confirmFile
    }

    useEffect(() => {
        if (nodes.length > 0) {
            const node = nodes[nodes.length - 1].original
            setFinalNode(node)
            //console.log("finalNode", node)
        }
    }, nodes)

    const showResult = (arr: any[], contentType: string) => {
        console.log(contentType)
        return (
            <div className={contentType === "text" ? css.resultTextContainer : css.resultContainer}>
                {arr.map((item: any,index) => {
                    return (
                        <div
                            key={index}
                            className={css.resultItem}
                        >
                            {contentType === "image" &&
                                <Image
                                    width={80}
                                    height={80}
                                    src={item}
                                />
                            }
                            {contentType === "video" &&
                                <div>
                                    <video controls src={item} style={{
                                        width: '100%', height: '200px'
                                    }} />
                                </div>
                            }
                            {contentType === "audio" &&
                                <div>
                                    <audio controls src={item} style={{
                                        height: '44px',marginTop: '8px'
                                    }} />
                                </div>
                            }
                            {(contentType === "text" || !contentType) &&
                                <div>
                                    <JsonView src={result}/>
                                </div>
                            }
                            {(contentType === "other" || contentType === "file") &&
                                <div>
                                    <img style={{width: '30px', height: '30px', marginRight: '8px'}} alt={""}
                                         src={getIcon(contentType)}/>
                                </div>
                            }
                            {(contentType && contentType !== "text") && <div>
                                <a href={item} target={"_blank"}>下载</a>
                            </div>}
                        </div>
                    )
                })
                }
            </div>
        )
    }

    return (
        <div>
            {finalNode.type === "endNode" &&
                <div>
                    {finalNode.data.outputDefs &&
                        <div>
                            {finalNode.data.outputDefs.map((outputDef: any) => {
                                return (
                                    <div key={outputDef.id}>
                                        {outputDef.name}
                                        {Array.isArray(result[outputDef.name]) ?
                                            showResult(result[outputDef.name], outputDef.contentType) :
                                            showResult([result[outputDef.name]],outputDef.contentType)}
                                    </div>
                                )
                            })
                            }
                        </div>
                    }
                    {!finalNode.data.outputDefs &&
                        <Empty description={'未定义输出'} style={{lineHeight: "280px"}}
                               image={Empty.PRESENTED_IMAGE_SIMPLE}/>
                    }
                </div>
            }
            {finalNode.type !== "endNode" &&
                <JsonView src={result}/>
            }
        </div>
    )
}