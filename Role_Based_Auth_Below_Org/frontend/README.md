## Frontend Project Structure Example
```
src/
├── main.jsx
├── App.jsx
│
├── app/                         # App-level setup
│   ├── router.jsx               # React Router config
│   ├── store.js                 # Zustand / Redux store
│   └── providers.jsx            # Theme, QueryClient, etc.
│
├── assets/                      # Images, icons, fonts
│
├── config/                      # Global configs
│   ├── env.js
│   ├── api.js                   # Axios instance
│   └── constants.js
│
├── common/                      # Shared reusable logic
│   ├── components/              # Buttons, Inputs, Modals
│   ├── hooks/                   # useAuth, useDebounce
│   ├── utils/                   # formatters, helpers
│   ├── services/                # API calls (generic)
│   └── types/                   # TS types (if TS)
│
├── modules/                     # Feature-based structure (IMPORTANT)
│   ├── user/
│   │   ├── pages/
│   │   │   ├── UserList.jsx
│   │   │   └── UserDetails.jsx
│   │   │
│   │   ├── components/
│   │   │   ├── UserCard.jsx
│   │   │   └── UserForm.jsx
│   │   │
│   │   ├── services/
│   │   │   └── user.api.js
│   │   │
│   │   ├── hooks/
│   │   │   └── useUser.js
│   │   │
│   │   └── store/
│   │       └── user.store.js
│   │
│   └── auth/
│       ├── pages/
│       ├── components/
│       ├── services/
│       └── hooks/
│
├── layouts/                     # Layouts
│   ├── MainLayout.jsx
│   └── AuthLayout.jsx
│
├── routes/                      # Route definitions (split)
│   └── index.jsx
│
├── styles/                      # Global styles
│   ├── index.css
│   └── theme.css
│
└── lib/                         # Third-party wrappers
    ├── axios.js
    └── react-query.js
```