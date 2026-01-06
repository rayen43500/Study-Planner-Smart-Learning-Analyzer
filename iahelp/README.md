# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:


## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type-aware lint rules:

```js
export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...

      // Remove tseslint.configs.recommended and replace with this
      tseslint.configs.recommendedTypeChecked,
      // Alternatively, use this for stricter rules
      tseslint.configs.strictTypeChecked,
      // Optionally, add this for stylistic rules
      tseslint.configs.stylisticTypeChecked,

      // Other configs...
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

You can also install [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) and [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) for React-specific lint rules:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...
      // Enable lint rules for React
      reactX.configs['recommended-typescript'],
      // Enable lint rules for React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])

## Gemini / modèle externe

Ce projet inclut une intégration client légère qui peut appeler un proxy serveur
pour interroger un modèle (ex: `gemini-1.5-flash`). Par défaut l'app fonctionne
en local sans modèle externe. Pour utiliser Gemini (ou tout modèle remote) :

- Créer un proxy serveur sécurisé (ne mettez jamais vos clés/API dans le frontend).
- Exemple de proxy Node/Express : `server/gemini-proxy-example.js` (utilise
  `@google-cloud/aiplatform`). Configure `GOOGLE_APPLICATION_CREDENTIALS`,
  `PROJECT_ID`, `LOCATION`, `MODEL_NAME` puis démarrez le serveur.
- Dans le frontend, configurez la variable d'environnement Vite `VITE_GEMINI_PROXY_URL`
  (par ex. `http://localhost:5178/chat`) avant de lancer l'app :

```powershell
setx VITE_GEMINI_PROXY_URL "http://localhost:5178/chat"
npm run dev
```

Le client enverra alors le texte de l'utilisateur au proxy et affichera la
réponse du modèle. Si `VITE_GEMINI_PROXY_URL` n'est pas défini, le chatbot
utilisera un fallback local.
```
