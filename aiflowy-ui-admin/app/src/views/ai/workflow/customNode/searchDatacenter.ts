import { getOptions } from '@aiflowy/utils';

import { api } from '#/api/request';
import { $t } from '#/locales';

export const SearchDatacenterNode = async () => {
  const res = await api.get('/api/v1/datacenterTable/list');

  return {
    title: $t('aiWorkflow.queryData'),
    group: 'base',
    description: $t('aiWorkflow.descriptions.queryData'),
    icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M11 2C15.968 2 20 6.032 20 11C20 15.968 15.968 20 11 20C6.032 20 2 15.968 2 11C2 6.032 6.032 2 11 2ZM11 18C14.8675 18 18 14.8675 18 11C18 7.1325 14.8675 4 11 4C7.1325 4 4 7.1325 4 11C4 14.8675 7.1325 18 11 18ZM19.4853 18.0711L22.3137 20.8995L20.8995 22.3137L18.0711 19.4853L19.4853 18.0711Z"></path></svg>',
    sortNo: 813,
    parametersAddEnable: true,
    outputDefsAddEnable: false,
    parameters: [],
    outputDefs: [
      {
        name: 'rows',
        title: $t('aiWorkflow.queryResult'),
        dataType: 'Array',
        dataTypeDisabled: true,
        required: true,
        parametersAddEnable: false,
        description: $t('aiWorkflow.queryResult'),
        deleteDisabled: true,
        nameDisabled: false,
      },
    ],
    forms: [
      {
        type: 'heading',
        label: $t('aiWorkflow.dataTable'),
      },
      {
        type: 'select',
        label: '',
        description: $t('aiWorkflow.descriptions.dataTable'),
        name: 'tableId',
        defaultValue: '',
        options: getOptions('tableName', 'id', res.data),
      },
      {
        type: 'heading',
        label: $t('aiWorkflow.filterConditions'),
      },
      {
        type: 'textarea',
        label: $t('aiWorkflow.descriptions.filterConditions'),
        description: '',
        name: 'where',
        defaultValue: '',
      },
      {
        type: 'heading',
        label: $t('aiWorkflow.limit'),
      },
      {
        type: 'input',
        label: '',
        description: '',
        name: 'limit',
        defaultValue: '10',
      },
    ],
  };
};
