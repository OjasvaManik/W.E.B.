import {useState, ChangeEvent, FormEvent, JSX} from "react";
import { Link, useNavigate } from "react-router-dom";
import apiService from "../../services/apiService.ts";

// Define TypeScript interfaces
interface LoginFormData {
    userName: string;
    _password: string;
}

interface LoginResponse {
    userId: string;
    userName: string;
    role: string;
    jwtToken: string;
}

export default function Login(): JSX.Element {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<LoginFormData>({
        userName: "",
        _password: ""
    });
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>("");

    const handleChange = (e: ChangeEvent<HTMLInputElement>): void => {
        const { name, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>): Promise<void> => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            // Using apiService instead of direct axios call
            const response = await apiService.post<LoginResponse, LoginFormData>(
                "/auth/login",
                formData
            );

            // Login successful
            console.log("Login successful:", response);

            // Store JWT token in localStorage for persistent login
            localStorage.setItem("jwtToken", response.jwtToken);
            localStorage.setItem("userName", response.userName);
            localStorage.setItem("userRole", response.role);
            // In your login component's handleSubmit function after successful login
            localStorage.setItem("userId", response.userId); // Add this to store userId

            // Redirect to home or dashboard page after successful login
            navigate("/wiki");
        } catch (err: any) {
            // Handle error from apiService
            const errorMessage = err.response?.data?.message || "Login failed. Please check your credentials.";
            setError(errorMessage);
            console.error("Login error:", err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex justify-center items-center mx-36 mt-24">
            <div className="w-3/4 bg-teal-700 rounded-2xl flex flex-col justify-center items-center gap-4 py-12">
                <div>
                    <p className="text-2xl text-white font-bold">LOGIN</p>
                </div>

                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-2 rounded">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="flex flex-col gap-4 items-center w-full">
                    <div>
                        <input
                            type="text"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            placeholder="Username"
                            className="w-96 bg-white rounded-lg text-blue-800 text-lg px-4 py-2 focus:outline-none"
                            required
                        />
                    </div>
                    <div>
                        <input
                            type="password"
                            name="_password"
                            value={formData._password}
                            onChange={handleChange}
                            placeholder="Password"
                            className="w-96 bg-white rounded-lg text-blue-800 text-lg px-4 py-2 focus:outline-none"
                            required
                        />
                    </div>
                    <div className="mt-2">
                        <button
                            type="submit"
                            className="px-6 py-2 bg-white text-teal-700 font-medium rounded hover:bg-gray-200 hover:cursor-pointer"
                            disabled={isLoading}
                        >
                            {isLoading ? "Logging in..." : "Login"}
                        </button>
                    </div>
                </form>

                <div className="mt-4 text-white">
                    Don't have an account? <Link to="/register" className="underline hover:text-blue-200">Register</Link>
                </div>
            </div>
        </div>
    );
}