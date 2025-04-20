import { useState, useEffect } from "react";
import { useSearchParams, Link } from "react-router-dom";
import axios from "axios";

interface SearchItem {
    id: string;
    title: string;
    content: string;
    userId: string;
    userName: string;
    type: "ARTICLE" | "POST";
    source?: string;
    commentCount?: number;
}

// API base URL - adjust this to match your backend server address
const API_BASE_URL = "http://localhost:8080"; // Adjust this to your actual backend URL

export default function SearchResults() {
    const [searchParams] = useSearchParams();
    const query = searchParams.get("q") || "";
    const [results, setResults] = useState<SearchItem[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [hasSearched, setHasSearched] = useState(false);

    useEffect(() => {
        const fetchSearchResults = async () => {
            if (!query) {
                setResults([]);
                return;
            }

            setLoading(true);
            setError("");
            setHasSearched(true);

            try {
                // Debug the request
                console.log(`Searching for: ${query}`);

                // Use the full URL with the API base URL
                const response = await axios.get(`${API_BASE_URL}/api/v1/web/search?searchTerm=${encodeURIComponent(query)}`, {
                    responseType: 'json',
                    headers: {
                        'Accept': 'application/json'
                    }
                });

                console.log("Response data:", response.data);

                // Safely handle the response
                if (response.data && Array.isArray(response.data)) {
                    setResults(response.data);
                } else if (response.data && response.data.results && Array.isArray(response.data.results)) {
                    // Handle case where API returns { results: [...] }
                    setResults(response.data.results);
                } else {
                    console.error("Unexpected response format:", response.data);
                    setError("Received an invalid response format from the server.");
                }
            } catch (err) {
                console.error("Search failed:", err);
                if (axios.isAxiosError(err)) {
                    if (err.response) {
                        console.error("Error status:", err.response.status);
                        console.error("Error data:", err.response.data);

                        if (err.response.status === 404) {
                            setError(`API endpoint not found. Make sure your backend server is running at ${API_BASE_URL} and the endpoint /api/v1/web/search exists.`);
                        } else {
                            setError(`Failed to fetch search results: ${err.response.status} ${err.response.statusText}`);
                        }
                    } else if (err.request) {
                        console.error("No response received:", err.request);
                        setError(`No response received from the server at ${API_BASE_URL}. Please check your connection and make sure the server is running.`);
                    } else {
                        console.error("Error message:", err.message);
                        setError(`Request error: ${err.message}`);
                    }
                } else {
                    console.error("Unknown error:", err);
                    setError("An unknown error occurred. Please try again.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchSearchResults();
    }, [query]);

    // Separate results by type
    const articles = results.filter(item => item.type === "ARTICLE");
    const posts = results.filter(item => item.type === "POST");

    if (loading) {
        return (
            <div className="max-w-4xl mx-auto mt-8 p-4">
                <h2 className="text-2xl font-bold mb-4">Searching for "{query}"...</h2>
                <div className="flex justify-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-teal-500"></div>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto mt-8 p-4">
            <h2 className="text-2xl font-bold mb-4">Search Results for "{query}"</h2>

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            {!error && results.length === 0 && hasSearched && (
                <p className="text-gray-500">No results found for your search query.</p>
            )}

            {!error && results.length === 0 && !query && (
                <p className="text-gray-500">Enter a search term to find articles and posts.</p>
            )}

            {results.length > 0 && (
                <div className="space-y-8">
                    {articles.length > 0 && (
                        <div>
                            <h3 className="text-xl font-semibold mb-4 text-teal-700">Articles</h3>
                            <div className="space-y-4">
                                {articles.map((article) => (
                                    <div key={article.id} className="bg-white p-4 rounded-lg shadow hover:shadow-md transition-shadow">
                                        <Link to={`/wiki/${article.id}`} className="block">
                                            <h4 className="text-lg font-medium text-teal-800 hover:text-teal-600">{article.title}</h4>
                                            <p className="text-gray-600 mt-2">{article.content.substring(0, 200)}{article.content.length > 200 ? '...' : ''}</p>
                                            <div className="mt-2 flex justify-between items-center text-sm text-gray-500">
                                                <span>By: {article.userName}</span>
                                                {article.source && <span>Source: {article.source}</span>}
                                            </div>
                                        </Link>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {posts.length > 0 && (
                        <div>
                            <h3 className="text-xl font-semibold mb-4 text-teal-700">Posts</h3>
                            <div className="space-y-4">
                                {posts.map((post) => (
                                    <div key={post.id} className="bg-white p-4 rounded-lg shadow hover:shadow-md transition-shadow">
                                        <Link to={`/forum/${post.id}`} className="block">
                                            <h4 className="text-lg font-medium text-teal-800 hover:text-teal-600">{post.title}</h4>
                                            <p className="text-gray-600 mt-2">{post.content.substring(0, 200)}{post.content.length > 200 ? '...' : ''}</p>
                                            <div className="mt-2 flex justify-between items-center text-sm text-gray-500">
                                                <span>By: {post.userName}</span>
                                                {post.commentCount !== undefined && (
                                                    <span>{post.commentCount} comment{post.commentCount !== 1 ? 's' : ''}</span>
                                                )}
                                            </div>
                                        </Link>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}