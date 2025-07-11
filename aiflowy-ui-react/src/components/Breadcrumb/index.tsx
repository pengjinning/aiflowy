import React from 'react';
import {Breadcrumb as AntdBreadcrumb, Flex} from 'antd';
import {useMenus} from "../../hooks/useMenus.tsx";
import {useLayout} from '../../hooks/useLayout.tsx';
import {BreadcrumbItemType} from "antd/es/breadcrumb/Breadcrumb";

/**
 * 面包屑组件
 */
const Breadcrumb: React.FC = () => {

    const {selectItems} = useMenus();
    const {options} = useLayout();

    const breadcrumbs: BreadcrumbItemType[] = [];
    if (options?.breadcrumbs && options.breadcrumbs.length > 0) {
        breadcrumbs.push(...options.breadcrumbs)
    } else {
        breadcrumbs.push(...selectItems.map((item) => {
            return {
                // title: t(item.label)
                title: item.label
            } as BreadcrumbItemType;
        }))
        breadcrumbs.unshift({title: "首页"})
    }

    const lastItem = breadcrumbs[breadcrumbs.length - 1];

    return (
        <Flex style={{alignItems: "center", height: "48px", backgroundColor: "white", padding: "0 24px", background: "#F2F2F2"}}>
            <h2 style={{margin: "0", padding: "0"}}>{lastItem?.title}</h2>
            <AntdBreadcrumb style={{marginLeft: '16px'}} items={breadcrumbs}/>
            <div style={{marginLeft: "auto", marginRight: "20px"}}>{options?.breadcrumbRightEl}</div>
        </Flex>
    );
};


export default Breadcrumb;