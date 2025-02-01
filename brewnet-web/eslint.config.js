import eslintConfigPrettier from 'eslint-config-prettier';
import importPlugin from 'eslint-plugin-import';
import pluginPrettier from 'eslint-plugin-prettier';
import pluginVue from 'eslint-plugin-vue';

export default [
  ...pluginVue.configs['flat/recommended'],
  importPlugin.flatConfigs.recommended,
  {
    plugins: {
      prettier: pluginPrettier,
    },
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
    },
    settings: {
      'import/resolver': {
        alias: {
          map: [
            ['@', './src'], // '@'을 './src'로 해석하도록 설정
          ],
          extensions: ['.js', '.vue', '.json'], // 확장자 설정
        },
        node: true,
      },
    },
    rules: {
      'prettier/prettier': [
        'error',
        {
          $schema: 'https://json.schemastore.org/prettierrc',
          singleQuote: true,
          semi: true,
          useTabs: false,
          tabWidth: 2,
          trailingComma: 'all',
          printWidth: 120,
          bracketSpacing: true,
          arrowParens: 'avoid',
          endOfLine: 'auto',
        },
      ],

      // import/order 규칙 설정
      'import/order': [
        'error',
        {
          groups: [
            ['builtin', 'external'], // node 모듈과 외부 라이브러리는 별도의 그룹으로
            'internal', // 내부 import는 따로
            ['sibling', 'parent'], // 형제 모듈과 부모 모듈은 같은 그룹
            'index', // index 파일은 마지막에
          ],
          'newlines-between': 'always', // 각 그룹 간에 빈 줄을 추가하도록 강제
          alphabetize: { order: 'asc', caseInsensitive: true }, // 알파벳순 정렬
        },
      ],
    },
  },
  eslintConfigPrettier,
];
