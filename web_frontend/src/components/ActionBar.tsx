import { useLocation, Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

export default function ActionBar() {
    const location = useLocation();
    const navigate = useNavigate();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userName, setUserName] = useState("");
    const [userRole, setUserRole] = useState("");

    useEffect(() => {
        // Check if user is logged in by looking for JWT token
        const token = localStorage.getItem("jwtToken");
        const storedUserName = localStorage.getItem("userName");
        const storedUserRole = localStorage.getItem("userRole");

        if (token && storedUserName) {
            setIsLoggedIn(true);
            setUserName(storedUserName);
            setUserRole(storedUserRole || "");
        } else {
            setIsLoggedIn(false);
        }
    }, [location]); // Re-check when location changes

    const handleLogout = () => {
        // Clear local storage
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("userName");
        localStorage.removeItem("userRole");
        localStorage.removeItem("userId");

        // Update state
        setIsLoggedIn(false);
        setUserName("");
        setUserRole("");

        // Navigate to home page
        navigate("/wiki");
    };

    // Determine the active tab based on the current path
    const getActiveTab = () => {
        const path = location.pathname;

        // Check specific paths first
        if (path === '/register') return 'register';
        if (path === '/login') return 'login';
        if (path === '/user') return 'user';
        if (path === '/forum') return 'forum';
        if (path === '/create/post') return 'post';

        // Handle wiki paths
        if (path === '/wiki/create') return 'article';
        if (path === '/wiki' || path === '/') return 'wiki';

        // Handle article view/edit paths
        if (path.startsWith('/wiki/')) return 'wiki';

        return '';
    };

    const activeTab = getActiveTab();

    return (
        <div className="bg-teal-700 px-10 h-16 rounded-lg flex items-center justify-center">
            <div className="flex items-center">
                {/* Create text with small vertical line */}
                <div className="flex items-center">
                    <span className="text-white font-medium mr-3">CREATE</span>
                    <div className="h-8 w-px bg-white/40 mr-3"></div>
                </div>

                <div className="flex space-x-3">
                    <Link to="/wiki/create">
                        <button className={`px-3 py-1 text-white hover:bg-teal-600 rounded hover:cursor-pointer ${activeTab === "article" ? "bg-teal-600" : ""}`}>
                            Article
                        </button>
                    </Link>
                    <Link to="/create/post">
                        <button className={`px-3 py-1 text-white hover:bg-teal-600 rounded hover:cursor-pointer ${activeTab === "post" ? "bg-teal-600" : ""}`}>
                            Post
                        </button>
                    </Link>
                </div>
            </div>

            <div className="ml-6 h-10 w-1 bg-white/70"></div>

            <div className="flex gap-4 ml-4 mr-4">
                {isLoggedIn ? (
                    <>
                        <Link to="/user">
                            <button className={`min-w-[100px] px-4 py-1 bg-white text-teal-700 rounded hover:bg-gray-200 hover:cursor-pointer ${activeTab === "user" ? "ring-2 ring-amber-400" : ""}`}>
                                {userName} ({userRole})
                            </button>
                        </Link>
                        <button
                            onClick={handleLogout}
                            className="min-w-[100px] px-4 py-1 bg-red-500 text-white rounded hover:bg-red-600 hover:cursor-pointer"
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/register">
                            <button className={`min-w-[100px] px-4 py-1 bg-white text-teal-700 rounded hover:bg-gray-200 hover:cursor-pointer ${activeTab === "register" ? "bg-amber-400" : ""}`}>
                                Register
                            </button>
                        </Link>
                        <Link to="/login">
                            <button className={`min-w-[100px] px-4 py-1 bg-white text-teal-700 rounded hover:bg-gray-200 hover:cursor-pointer ${activeTab === "login" ? "ring-2 ring-white" : ""}`}>
                                Login
                            </button>
                        </Link>
                    </>
                )}
            </div>

            <div className="h-10 w-1 mr-6 bg-white/70"></div>

            <div className="flex items-center space-x-4">
                <Link to="/wiki">
                    <button className={`px-3 py-1 text-white hover:bg-teal-600 rounded hover:cursor-pointer ${activeTab === "wiki" ? "bg-teal-600" : ""}`}>
                        Wiki
                    </button>
                </Link>
                <Link to="/forum">
                    <button className={`px-3 py-1 text-white hover:bg-teal-600 rounded hover:cursor-pointer ${activeTab === "forum" ? "bg-teal-600" : ""}`}>
                        Forum
                    </button>
                </Link>
            </div>
        </div>
    );
}