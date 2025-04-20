import NavBar from "./components/NavBar.tsx";
import ActionBar from "./components/ActionBar.tsx";
import {Outlet} from "react-router-dom";

function App() {


  return (
    <div>
        <div className={'flex flex-col items-center gap-4'}>
            <NavBar />
            <ActionBar />
        </div>
        <Outlet />
    </div>
  )
}

export default App
