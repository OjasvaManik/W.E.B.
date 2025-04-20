import React, { useState, useEffect } from "react";
import MDEditor from "@uiw/react-md-editor";
import { useNavigate, useParams } from "react-router-dom";
import apiService from "../../services/apiService";

// Define TypeScript interfaces
interface ArticlePayload {
    articleTitle: string;
    articleContent: string;
    articleSource: string;
    userId: string;
    categoryIds: string[];
}

interface Category {
    categoryId: string;
    categoryName: string;
    categoryDescription: string;
}

interface Article {
    articleId: string;
    articleTitle: string;
    articleContent: string;
    articleSource: string;
    userId: string;
    articleStatus: {
        status: string;
    };
    categoryIds?: string[]; // This may be returned from the API if available
}

const EditArticle: React.FC = () => {
    const navigate = useNavigate();
    const { articleId } = useParams<{ articleId: string }>();

    const [content, setContent] = useState<string>("");
    const [title, setTitle] = useState<string>("");
    const [source, setSource] = useState<string>("");
    const [categoryIds, setCategoryIds] = useState<string[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isFetchingArticle, setIsFetchingArticle] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [categories, setCategories] = useState<Category[]>([]);
    const [isLoadingCategories, setIsLoadingCategories] = useState<boolean>(false);

    // Check authentication on component mount
    useEffect(() => {
        const jwtToken = localStorage.getItem("jwtToken");
        const userName = localStorage.getItem("userName");

        if (!jwtToken || !userName) {
            // User is not logged in, redirect to login page
            navigate("/login", { state: { from: location.pathname } });
        } else {
            // Fetch categories and article data if user is authenticated
            fetchCategories();
            fetchArticleData();
        }
    }, [navigate, articleId]);

    // Get user ID from localStorage
    const userId = localStorage.getItem("userId") || "";
    const userName = localStorage.getItem("userName") || "";

    // Fetch categories from API
    const fetchCategories = async () => {
        setIsLoadingCategories(true);
        try {
            const response = await apiService.get<Category[]>("/wiki/create/categories");
            setCategories(response);
        } catch (err) {
            console.error("Error fetching categories:", err);
            setError("Failed to load categories. Please refresh the page.");
        } finally {
            setIsLoadingCategories(false);
        }
    };

    // Fetch existing article data
    const fetchArticleData = async () => {
        setIsFetchingArticle(true);
        try {
            const response = await apiService.get<Article>(`/wiki/view/${articleId}`);
            setTitle(response.articleTitle);
            setContent(response.articleContent);
            setSource(response.articleSource);

            // If categoryIds is available directly in the response
            if (response.categoryIds) {
                setCategoryIds(response.categoryIds);
            }
            // Note: You might need to adapt this depending on how category data is returned

        } catch (err) {
            console.error("Error fetching article:", err);
            setError("Failed to load article data. Please try again.");
        } finally {
            setIsFetchingArticle(false);
        }
    };

    // Handle category selection
    const handleCategoryChange = (categoryId: string) => {
        setCategoryIds(prevIds => {
            if (prevIds.includes(categoryId)) {
                // If already selected, remove it
                return prevIds.filter(id => id !== categoryId);
            } else {
                // If not selected, add it
                return [...prevIds, categoryId];
            }
        });
    };

    // Handle submit update
    const handleSubmit = async () => {
        if (!title || !content) {
            setError("Please fill all the required fields.");
            return;
        }

        const payload: ArticlePayload = {
            articleTitle: title,
            articleContent: content,
            articleSource: source || "N/A",
            userId: userId,
            categoryIds: categoryIds,
        };

        setIsLoading(true);
        setError("");

        try {
            await apiService.post(`/article/${articleId}/update`, payload);
            alert("Article updated successfully!");
            navigate(`/wiki/${articleId}`); // Navigate back to article view page
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || "Failed to update article. Please try again.";
            setError(errorMessage);
            console.error("Article update error:", err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container mx-auto p-4 space-y-6">
            <h1 className="text-2xl font-bold text-teal-700">Edit Article</h1>

            {userName && (
                <p className="text-gray-600">Editing as: <span className="font-medium">{userName}</span></p>
            )}

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded">
                    {error}
                </div>
            )}

            {isFetchingArticle ? (
                <div className="text-center py-8">
                    <p>Loading article data...</p>
                </div>
            ) : (
                <>
                    {/* Title Input */}
                    <div>
                        <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
                            Article Title *
                        </label>
                        <input
                            id="title"
                            type="text"
                            placeholder="Enter article title"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className="w-full p-2 border rounded text-gray-600 focus:ring-teal-500 focus:border-teal-500"
                            required
                        />
                    </div>

                    {/* Source Input */}
                    <div>
                        <label htmlFor="source" className="block text-sm font-medium text-gray-700 mb-1">
                            Article Source (optional)
                        </label>
                        <input
                            id="source"
                            type="text"
                            placeholder="Enter article source"
                            value={source}
                            onChange={(e) => setSource(e.target.value)}
                            className="w-full p-2 border rounded text-gray-600 focus:ring-teal-500 focus:border-teal-500"
                        />
                    </div>

                    {/* Category Selection */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Select Categories
                        </label>
                        {isLoadingCategories ? (
                            <p>Loading categories...</p>
                        ) : (
                            <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
                                {categories.map(category => (
                                    <div key={category.categoryId} className="flex items-center">
                                        <input
                                            type="checkbox"
                                            id={`category-${category.categoryId}`}
                                            checked={categoryIds.includes(category.categoryId)}
                                            onChange={() => handleCategoryChange(category.categoryId)}
                                            className="mr-2"
                                        />
                                        <label htmlFor={`category-${category.categoryId}`} className="cursor-pointer text-gray-600">
                                            {category.categoryName}
                                        </label>
                                    </div>
                                ))}
                            </div>
                        )}
                        {categories.length === 0 && !isLoadingCategories && (
                            <p className="text-gray-500">No categories available</p>
                        )}
                    </div>

                    {/* Markdown Editor */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Article Content *
                        </label>
                        <MDEditor
                            value={content}
                            onChange={(val) => setContent(val || "")}
                            height={400}
                        />
                    </div>

                    {/* Submit Button */}
                    <div className="flex gap-4">
                        <button
                            onClick={handleSubmit}
                            disabled={isLoading}
                            className="px-6 py-2 bg-teal-700 text-white font-medium rounded hover:bg-teal-800 disabled:bg-gray-400"
                        >
                            {isLoading ? "Updating..." : "Update Article"}
                        </button>

                        <button
                            onClick={() => navigate(`/wiki/${articleId}`)}
                            className="px-6 py-2 bg-gray-200 text-gray-700 font-medium rounded hover:bg-gray-300"
                        >
                            Cancel
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default EditArticle;