import { $t } from '#/locales';

import nodeNames from './nodeNames';

export default {
  [nodeNames.documentNode]: {
    title: $t('aiWorkflow.fileContentExtraction'),
    group: 'base',
    description: $t('aiWorkflow.descriptions.fileContentExtraction'),
    icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M21 4V18.7215C21 18.9193 20.8833 19.0986 20.7024 19.1787L12 23.0313L3.29759 19.1787C3.11667 19.0986 3 18.9193 3 18.7215V4H1V2H23V4H21ZM5 4V17.7451L12 20.8441L19 17.7451V4H5ZM8 8H16V10H8V8ZM8 12H16V14H8V12Z"></path></svg>',
    sortNo: 801,
    parametersAddEnable: false,
    outputDefsAddEnable: false,
    parameters: [
      {
        name: 'fileUrl',
        nameDisabled: true,
        title: $t('aiWorkflow.documentAddress'),
        dataType: 'File',
        required: true,
        description: $t('aiWorkflow.descriptions.documentAddress'),
      },
    ],
    outputDefs: [
      {
        name: 'content',
        title: $t('aiWorkflow.parsedText'),
        dataType: 'String',
        dataTypeDisabled: true,
        required: true,
        parametersAddEnable: false,
        description: $t('aiWorkflow.descriptions.parsedText'),
        deleteDisabled: true,
      },
    ],
  },
};
