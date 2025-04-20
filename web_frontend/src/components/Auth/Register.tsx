import {useState, ChangeEvent, FormEvent, JSX} from "react";
import { Link, useNavigate } from "react-router-dom";
import apiService from "../../services/apiService.ts";

// Define TypeScript interfaces
interface RegisterFormData {
    firstName: string;
    lastName: string;
    email: string;
    userName: string;
    password: string;
}

interface RegisterResponse {
    // Define what your API returns on successful registration
    success: boolean;
    message?: string;
}

export default function Register(): JSX.Element {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<RegisterFormData>({
        firstName: "",
        lastName: "",
        email: "",
        userName: "",
        password: ""
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
            const response = await apiService.post<RegisterResponse, RegisterFormData>(
                "/auth/register",
                formData
            );

            // Registration successful
            console.log("Registration successful:", response);

            // Redirect to login page after successful registration
            navigate("/login");
        } catch (err: any) {
            // Handle error from apiService
            const errorMessage = err.response?.data?.message || "Registration failed. Please try again.";
            setError(errorMessage);
            console.error("Registration error:", err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex justify-center items-center mx-36 mt-24">
            <div className="w-3/4 bg-teal-700 rounded-2xl flex flex-col justify-center items-center gap-4 py-12">
                <div>
                    <p className="text-2xl text-white font-bold">CREATE ACCOUNT</p>
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
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            placeholder="First Name"
                            className="w-96 bg-white rounded-lg text-blue-800 text-lg px-4 py-2 focus:outline-none"
                            required
                        />
                    </div>
                    <div>
                        <input
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            placeholder="Last Name"
                            className="w-96 bg-white rounded-lg text-blue-800 text-lg px-4 py-2 focus:outline-none"
                            required
                        />
                    </div>
                    <div>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Email"
                            className="w-96 bg-white rounded-lg text-blue-800 text-lg px-4 py-2 focus:outline-none"
                            required
                        />
                    </div>
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
                            name="password"
                            value={formData.password}
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
                            {isLoading ? "Registering..." : "Submit"}
                        </button>
                    </div>
                </form>

                <div className="mt-4 text-white">
                    Already have an account? <Link to="/login" className="underline hover:text-blue-200">Log in</Link>
                </div>
            </div>
        </div>
    );
}