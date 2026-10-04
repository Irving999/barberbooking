import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import SlotsPage from "./pages/SlotsPage";

function Placeholder() {
  return <div style={{ padding: "40px" }}>Coming soon</div>;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/slots" element={<SlotsPage />} />

        <Route path="/signup" element={<Placeholder />} />
        <Route path="/booking" element={<Placeholder />} />
        <Route path="/confirmation" element={<Placeholder />} />
        <Route path="/appointments" element={<Placeholder />} />
        <Route path="/provider" element={<Placeholder />} />
      </Routes>
    </BrowserRouter>
  );
}