import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function NavBar() {
    const [searchTerm, setSearchTerm] = useState("");
    const navigate = useNavigate();

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        if (searchTerm.trim()) {
            navigate(`/search?q=${encodeURIComponent(searchTerm.trim())}`);
        }
    };

    return (
        <div className="w-full bg-teal-700 flex items-center gap-4 justify-center h-20">
            <div>
                <Link to="/wiki">
                    <p className="font-[AllertaStencil] text-4xl">W.E.B.</p>
                </Link>
            </div>
            <form onSubmit={handleSearch} className="flex gap-3 items-center justify-center">
                <input
                    type="search"
                    placeholder="Search"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-[400px] bg-white h-[30] rounded-[15px] text-[#395E78] text-[20px] px-4 py-2 focus:outline-0 focus:ring-0"
                />
                <div className="w-11 h-11 bg-gradient-to-r from-[#999999] to-[#FFFFFF] rounded-full flex justify-center items-center hover:opacity-75">
                    <button
                        type="submit"
                        className="w-[38px] h-[38px] bg-gradient-to-r from-[#00DFC5] to-[#00796B] rounded-full flex items-center justify-center hover:cursor-pointer"
                    >
                        <img src="images/icons/search.png" alt="search" className="w-7 h-7" />
                    </button>
                </div>
            </form>
        </div>
    );
}