import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'

import App from "./App.tsx";
import Wiki from "./components/Wiki/Wiki.tsx";
import Register from "./components/Auth/Register.tsx";
import Login from "./components/Auth/Login.tsx";
import UserProfile from "./components/User/UserProfile.tsx";
import CreateArticle from "./components/Wiki/CreateArticle.tsx";
import ViewArticle from "./components/Wiki/ViewArticle.tsx";
import EditArticle from "./components/Wiki/EditArticle.tsx";
import CreatePost from "./components/Forum/CreatePost.tsx";
import Forum from "./components/Forum/Forum.tsx";
import ViewPost from "./components/Forum/ViewPost.tsx";
import SearchResults from "./components/SearchResults.tsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        children: [
            {
                index: true,
                element: <Wiki />
            },
            {
                path: 'wiki',
                element: <Wiki />
            },
            {
                path: 'wiki/create',
                element: <CreateArticle />
            },
            {
                path: 'wiki/:articleId',
                element: <ViewArticle />
            },
            {
                path: 'wiki/:articleId/edit',
                element: <EditArticle />
            },
            {
                path: 'register',
                element: <Register />
            },
            {
                path: 'login',
                element: <Login />
            },
            {
                path: 'user',
                element: <UserProfile />
            },
            {
                path: 'create/post',
                element: <CreatePost />
            },
            {
                path: 'forum',
                element: <Forum />
            },
            {
                path: 'forum/:postId',
                element: <ViewPost />
            },
            {
                path: 'search',
                element: <SearchResults />
            }
        ]
    }
])

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <RouterProvider router={router} />
    </StrictMode>
)