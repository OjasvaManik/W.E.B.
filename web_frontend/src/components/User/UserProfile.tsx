import { useState, useEffect, ChangeEvent, FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import apiService from "../../services/apiService";

interface UserData {
    userId: string;
    userName: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    profileImageUrl: string | null;
    isBanned: boolean;
}

interface CategoryData {
    categoryId: string;
    categoryName: string;
    categoryDescription: string;
}

interface ArticleStatus {
    status: "APPROVED" | "PENDING" | "REJECTED";
}

interface ArticleData {
    articleId: string;
    articleTitle: string;
    articleContent: string;
    articleSource: string;
    userId: string;
    categoryName: string[];
    articleStatus: ArticleStatus;
}

export default function UserProfile() {
    const navigate = useNavigate();
    const [userData, setUserData] = useState<UserData | null>(null);
    const [isAdmin, setIsAdmin] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    // Admin panel states
    const [activeTab, setActiveTab] = useState<'users' | 'wiki'>('users');
    const [userSearchQuery, setUserSearchQuery] = useState("");
    const [usersList, setUsersList] = useState<UserData[]>([]);
    const [categories, setCategories] = useState<CategoryData[]>([]);
    const [articles, setArticles] = useState<ArticleData[]>([]);
    const [articleViewMode, setArticleViewMode] = useState<'pending' | 'approved' | 'rejected'>('pending');
    const [newCategory, setNewCategory] = useState({
        categoryName: "",
        categoryDescription: ""
    });

    // Loading states
    const [loadingUsers, setLoadingUsers] = useState(false);
    const [loadingCategories, setLoadingCategories] = useState(false);
    const [loadingArticles, setLoadingArticles] = useState(false);
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        const fetchUserData = async () => {
            // Check if user is logged in
            const token = localStorage.getItem("jwtToken");
            const userName = localStorage.getItem("userName");

            if (!token || !userName) {
                navigate("/login");
                return;
            }

            try {
                setIsLoading(true);
                const response = await apiService.get<UserData[]>(
                    `/user/all-users?userName=${userName}`
                );

                if (response && response.length > 0) {
                    setUserData(response[0]);
                    // Check if user is admin
                    setIsAdmin(response[0].role === "ADMIN");

                    // If admin, fetch initial data
                    if (response[0].role === "ADMIN") {
                        fetchInitialData();
                    }
                } else {
                    setError("User data not found.");
                }
            } catch (err: any) {
                console.error("Error fetching user data:", err);
                const errorMessage = err.response?.data?.message || "Failed to load user data. Please try again later.";
                setError(errorMessage);
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserData();
    }, [navigate]);

    const fetchInitialData = async () => {
        setLoadingUsers(true);
        setLoadingCategories(true);
        setLoadingArticles(true);

        try {
            // Fetch users list
            const usersResponse = await apiService.get<UserData[]>(`/admin/users?userName=`);
            setUsersList(usersResponse || []);

            // Fetch categories
            const categoriesResponse = await apiService.get<CategoryData[]>(`/wiki/create/categories`);
            setCategories(categoriesResponse || []);

            // Fetch articles
            const articlesResponse = await apiService.get<ArticleData[]>(`/wiki`);
            setArticles(articlesResponse || []);
        } catch (err) {
            console.error("Error fetching admin data:", err);
        } finally {
            setLoadingUsers(false);
            setLoadingCategories(false);
            setLoadingArticles(false);
        }
    };

    const handleUserSearch = async () => {
        setLoadingUsers(true);
        try {
            const response = await apiService.get<UserData[]>(`/admin/users?userName=${userSearchQuery}`);
            setUsersList(response || []);
        } catch (err) {
            console.error("Error searching users:", err);
        } finally {
            setLoadingUsers(false);
        }
    };

    const handleToggleBan = async (userId: string, isBanned: boolean) => {
        setActionLoading(true);
        try {
            await apiService.put(`/admin/${userId}/toggle-ban`, {});

            // Update the user in the list
            setUsersList(prevUsers =>
                prevUsers.map(user =>
                    user.userId === userId
                        ? { ...user, isBanned: !isBanned }
                        : user
                )
            );
        } catch (err) {
            console.error("Error toggling ban status:", err);
        } finally {
            setActionLoading(false);
        }
    };

    const handleCategoryInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setNewCategory(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleAddCategory = async (e: FormEvent) => {
        e.preventDefault();
        setActionLoading(true);

        try {
            const response = await apiService.post('/admin/categories', newCategory);
            setCategories(prev => [...prev, response]);

            // Reset form
            setNewCategory({
                categoryName: "",
                categoryDescription: ""
            });
        } catch (err) {
            console.error("Error adding category:", err);
        } finally {
            setActionLoading(false);
        }
    };

    const handleApproveArticle = async (articleId: string) => {
        setActionLoading(true);
        try {
            await apiService.put(`/admin/articles/${articleId}/approve`, {});

            // Update the article in the list
            setArticles(prevArticles =>
                prevArticles.map(article =>
                    article.articleId === articleId
                        ? { ...article, articleStatus: { status: "APPROVED" } }
                        : article
                )
            );
        } catch (err) {
            console.error("Error approving article:", err);
        } finally {
            setActionLoading(false);
        }
    };

    const handleRejectArticle = async (articleId: string) => {
        setActionLoading(true);
        try {
            await apiService.put(`/admin/articles/${articleId}/reject`, {});

            // Update the article in the list
            setArticles(prevArticles =>
                prevArticles.map(article =>
                    article.articleId === articleId
                        ? { ...article, articleStatus: { status: "REJECTED" } }
                        : article
                )
            );
        } catch (err) {
            console.error("Error rejecting article:", err);
        } finally {
            setActionLoading(false);
        }
    };

    // Filter articles based on the current view mode
    const filteredArticles = articles.filter(article =>
        article.articleStatus.status === articleViewMode.toUpperCase()
    );

    // Count articles by status for tab badges
    const pendingCount = articles.filter(a => a.articleStatus.status === "PENDING").length;
    const approvedCount = articles.filter(a => a.articleStatus.status === "APPROVED").length;
    const rejectedCount = articles.filter(a => a.articleStatus.status === "REJECTED").length;

    if (isLoading) {
        return (
            <div className="flex justify-center items-center h-96">
                <div className="text-xl text-teal-700">Loading user data...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex justify-center items-center h-96">
                <div className="bg-red-100 border border-red-400 text-red-700 px-6 py-4 rounded-lg">
                    <p className="font-medium">{error}</p>
                    <button
                        onClick={() => navigate("/wiki")}
                        className="mt-4 px-4 py-2 bg-teal-700 text-white rounded hover:bg-teal-800"
                    >
                        Return to Wiki
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <div className={`grid ${isAdmin ? 'lg:grid-cols-2' : 'lg:grid-cols-1'} grid-cols-1 gap-6`}>
                {/* User Profile Card */}
                <div className="bg-white shadow-lg rounded-lg overflow-hidden">
                    <div className="bg-teal-700 text-white p-6">
                        <h1 className="text-2xl font-bold">User Profile</h1>
                    </div>

                    <div className="p-6">
                        <div className="flex flex-col md:flex-row md:items-center gap-6 mb-8">
                            <div className="w-28 h-28 rounded-full bg-teal-100 flex items-center justify-center text-teal-700 text-3xl font-bold">
                                {userData?.firstName?.charAt(0) || ''}
                                {userData?.lastName?.charAt(0) || ''}
                            </div>

                            <div>
                                <h2 className="text-2xl font-bold text-gray-800">
                                    {userData?.firstName} {userData?.lastName}
                                </h2>
                                <p className="text-gray-600 text-lg mt-1">@{userData?.userName}</p>
                                <div className="mt-3">
                                    <span className="inline-block bg-teal-100 text-teal-800 px-3 py-1 rounded-full text-sm font-medium">
                                        {userData?.role}
                                    </span>
                                    {userData?.isBanned ? (
                                        <span className="inline-block bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm font-medium ml-2">
                                            Banned
                                        </span>
                                    ) : (
                                        <span className="inline-block bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-medium ml-2">
                                            Active
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="border-t border-gray-200 pt-6">
                            <h3 className="text-lg font-semibold text-gray-700 mb-4">User Information</h3>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <p className="text-gray-500 text-sm">User ID</p>
                                    <p className="text-gray-800 font-medium">
                                        {userData?.userId ? `••••${userData.userId.slice(-4)}` : 'N/A'}
                                    </p>
                                </div>

                                <div>
                                    <p className="text-gray-500 text-sm">Email</p>
                                    <p className="text-gray-800 font-medium">{userData?.email}</p>
                                </div>

                                <div>
                                    <p className="text-gray-500 text-sm">Username</p>
                                    <p className="text-gray-800 font-medium">{userData?.userName}</p>
                                </div>

                                <div>
                                    <p className="text-gray-500 text-sm">Full Name</p>
                                    <p className="text-gray-800 font-medium">{userData?.firstName} {userData?.lastName}</p>
                                </div>
                            </div>
                        </div>

                        <div className="mt-8 flex justify-end">
                            <button
                                onClick={() => navigate("/wiki")}
                                className="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
                            >
                                Back to Wiki
                            </button>
                        </div>
                    </div>
                </div>

                {/* Admin Panel - Only show if user is admin */}
                {isAdmin && (
                    <div className="bg-white shadow-lg rounded-lg overflow-hidden">
                        <div className="bg-teal-700 text-white p-6">
                            <h1 className="text-2xl font-bold">Admin Panel</h1>
                        </div>

                        <div className="p-6">
                            {/* Admin Tab Navigation */}
                            <div className="flex border-b border-gray-200 mb-6">
                                <button
                                    onClick={() => setActiveTab('users')}
                                    className={`py-2 px-4 font-medium ${activeTab === 'users' ? 'text-teal-700 border-b-2 border-teal-700' : 'text-gray-500'}`}
                                >
                                    Users
                                </button>
                                <button
                                    onClick={() => setActiveTab('wiki')}
                                    className={`py-2 px-4 font-medium ${activeTab === 'wiki' ? 'text-teal-700 border-b-2 border-teal-700' : 'text-gray-500'}`}
                                >
                                    Wiki
                                </button>
                            </div>

                            {/* Users Tab */}
                            {activeTab === 'users' && (
                                <div>
                                    <div className="flex mb-4">
                                        <input
                                            type="text"
                                            placeholder="Search by username"
                                            value={userSearchQuery}
                                            onChange={(e) => setUserSearchQuery(e.target.value)}
                                            className="flex-grow px-4 py-2 border border-gray-300 rounded-l text-[#395E78] focus:outline-none focus:ring-1 focus:ring-teal-700"
                                        />
                                        <button
                                            onClick={handleUserSearch}
                                            className="px-4 py-2 bg-teal-700 text-white rounded-r hover:bg-teal-800"
                                            disabled={loadingUsers}
                                        >
                                            {loadingUsers ? "Searching..." : "Search"}
                                        </button>
                                    </div>

                                    <div className="mt-4 max-h-96 overflow-y-auto">
                                        {loadingUsers ? (
                                            <div className="flex justify-center py-4">
                                                <p>Loading users...</p>
                                            </div>
                                        ) : usersList.length > 0 ? (
                                            <div className="divide-y divide-gray-200">
                                                {usersList.map(user => (
                                                    <div key={user.userId} className="py-3 flex justify-between items-center">
                                                        <div>
                                                            <p className="font-medium text-gray-800">{user.firstName} {user.lastName}</p>
                                                            <p className="text-sm text-gray-600">@{user.userName}</p>
                                                            <p className="text-sm text-gray-500">{user.email}</p>
                                                            <span className={`inline-block px-2 py-1 text-xs rounded-full mt-1 ${user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'}`}>
                                                                {user.role}
                                                            </span>
                                                        </div>
                                                        <button
                                                            onClick={() => handleToggleBan(user.userId, user.isBanned)}
                                                            className={`px-4 py-1 rounded text-white ${user.isBanned ? 'bg-green-600 hover:bg-green-700' : 'bg-red-600 hover:bg-red-700'}`}
                                                            disabled={actionLoading}
                                                        >
                                                            {user.isBanned ? 'Unban' : 'Ban'}
                                                        </button>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <p className="text-center text-gray-500 py-4">No users found.</p>
                                        )}
                                    </div>
                                </div>
                            )}

                            {/* Wiki Tab */}
                            {activeTab === 'wiki' && (
                                <div>
                                    {/* Categories Section */}
                                    <div className="mb-8">
                                        <h3 className="text-lg font-semibold text-gray-700 mb-3">Categories</h3>

                                        {/* List of Categories */}
                                        <div className="mb-4 max-h-40 overflow-y-auto bg-gray-50 p-3 rounded">
                                            {loadingCategories ? (
                                                <p className="text-center py-2">Loading categories...</p>
                                            ) : categories.length > 0 ? (
                                                <div className="flex flex-wrap gap-2">
                                                    {categories.map(category => (
                                                        <span key={category.categoryId} className="bg-teal-100 text-teal-800 px-3 py-1 rounded-full text-sm">
                                                            {category.categoryName}
                                                        </span>
                                                    ))}
                                                </div>
                                            ) : (
                                                <p className="text-center text-gray-500 py-2">No categories found.</p>
                                            )}
                                        </div>

                                        {/* Add Category Form */}
                                        <form onSubmit={handleAddCategory} className="bg-gray-50 p-4 rounded">
                                            <h4 className="font-medium text-gray-700 mb-2">Add New Category</h4>
                                            <div className="mb-3">
                                                <input
                                                    type="text"
                                                    name="categoryName"
                                                    value={newCategory.categoryName}
                                                    onChange={handleCategoryInputChange}
                                                    placeholder="Category Name"
                                                    className="w-full px-3 py-2 border text-[#395E78] border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-teal-700"
                                                    required
                                                />
                                            </div>
                                            <div className="mb-3">
                                                <textarea
                                                    name="categoryDescription"
                                                    value={newCategory.categoryDescription}
                                                    onChange={handleCategoryInputChange}
                                                    placeholder="Category Description"
                                                    className="w-full px-3 py-2 border text-[#395E78] border-gray-300 rounded focus:outline-none focus:ring-1 focus:ring-teal-700"
                                                    rows={2}
                                                    required
                                                ></textarea>
                                            </div>
                                            <button
                                                type="submit"
                                                className="px-4 py-2 bg-teal-700 text-white rounded hover:bg-teal-800"
                                                disabled={actionLoading}
                                            >
                                                {actionLoading ? "Adding..." : "Add Category"}
                                            </button>
                                        </form>
                                    </div>

                                    {/* Articles Section */}
                                    <div>
                                        <h3 className="text-lg font-semibold text-gray-700 mb-3">Articles</h3>

                                        {/* Article Status Tabs */}
                                        <div className="flex mb-4 border-b border-gray-200">
                                            <button
                                                onClick={() => setArticleViewMode('pending')}
                                                className={`py-2 px-4 font-medium ${articleViewMode === 'pending' ? 'text-teal-700 border-b-2 border-teal-700' : 'text-gray-500'}`}
                                            >
                                                Pending ({pendingCount})
                                            </button>
                                            <button
                                                onClick={() => setArticleViewMode('approved')}
                                                className={`py-2 px-4 font-medium ${articleViewMode === 'approved' ? 'text-teal-700 border-b-2 border-teal-700' : 'text-gray-500'}`}
                                            >
                                                Approved ({approvedCount})
                                            </button>
                                            <button
                                                onClick={() => setArticleViewMode('rejected')}
                                                className={`py-2 px-4 font-medium ${articleViewMode === 'rejected' ? 'text-teal-700 border-b-2 border-teal-700' : 'text-gray-500'}`}
                                            >
                                                Rejected ({rejectedCount})
                                            </button>
                                        </div>

                                        <div className="max-h-96 overflow-y-auto">
                                            {loadingArticles ? (
                                                <p className="text-center py-4">Loading articles...</p>
                                            ) : filteredArticles.length > 0 ? (
                                                <div className="space-y-4">
                                                    {filteredArticles.map(article => (
                                                        <div key={article.articleId} className="border border-gray-200 rounded p-4">
                                                            <div className="flex justify-between items-start mb-2">
                                                                <h4 className="font-medium text-lg text-gray-800">{article.articleTitle}</h4>
                                                                <span className={`px-2 py-1 text-xs rounded-full ${
                                                                    article.articleStatus.status === 'APPROVED'
                                                                        ? 'bg-green-100 text-green-800'
                                                                        : article.articleStatus.status === 'REJECTED'
                                                                            ? 'bg-red-100 text-red-800'
                                                                            : 'bg-yellow-100 text-yellow-800'
                                                                }`}>
                                                                    {article.articleStatus.status}
                                                                </span>
                                                            </div>

                                                            <p className="text-sm text-gray-600 mb-2 line-clamp-2">
                                                                {article.articleContent}
                                                            </p>

                                                            <div className="flex flex-wrap gap-1 mb-3">
                                                                {article.categoryName.map((cat, index) => (
                                                                    <span key={index} className="bg-gray-100 text-gray-700 px-2 py-0.5 rounded text-xs">
                                                                        {cat}
                                                                    </span>
                                                                ))}
                                                            </div>

                                                            <div className="text-xs text-gray-500 mb-3">
                                                                Source: {article.articleSource}
                                                            </div>

                                                            {article.articleStatus.status === 'PENDING' && (
                                                                <div className="flex gap-2 justify-end">
                                                                    <button
                                                                        onClick={() => handleApproveArticle(article.articleId)}
                                                                        className="px-3 py-1 bg-green-600 text-white rounded text-sm hover:bg-green-700"
                                                                        disabled={actionLoading}
                                                                    >
                                                                        Approve
                                                                    </button>
                                                                    <button
                                                                        onClick={() => handleRejectArticle(article.articleId)}
                                                                        className="px-3 py-1 bg-red-600 text-white rounded text-sm hover:bg-red-700"
                                                                        disabled={actionLoading}
                                                                    >
                                                                        Reject
                                                                    </button>
                                                                </div>
                                                            )}
                                                        </div>
                                                    ))}
                                                </div>
                                            ) : (
                                                <p className="text-center text-gray-500 py-4">
                                                    No {articleViewMode} articles found.
                                                </p>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}