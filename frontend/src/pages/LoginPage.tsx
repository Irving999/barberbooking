import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";

type LoginResponse = {
  userId: number;
  email: string;
  role: string;
};

export default function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin() {
    setMessage("");
    setLoading(true);

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          email,
          password,
        }),
      });

      if (!response.ok) {
        setMessage("Invalid email or password.");
        return;
      }

      const user: LoginResponse = await response.json();

      sessionStorage.setItem(
        "loggedInUser",
        JSON.stringify(user)
      );

      if (user.role === "PROVIDER") {
        navigate("/provider");
      } else {
        navigate("/slots");
      }
    } catch {
      setMessage("Unable to connect to the server.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-shell">
      <Navbar />

      <main className="page-content">
        <div className="page-title">
          <h1>Login</h1>
          <p>Sign in to manage your appointments.</p>
        </div>

        <div className="form-group">
          <label htmlFor="email">Email</label>

          <input
            id="email"
            className="form-control"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder="Email"
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>

          <input
            id="password"
            className="form-control"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="Password"
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                handleLogin();
              }
            }}
          />
        </div>

        <div className="message-box">
          {message}
        </div>

        <button
          className="primary-button"
          type="button"
          onClick={handleLogin}
          disabled={loading}
        >
          {loading ? "Logging in..." : "Login"}
        </button>
      </main>
    </div>
  );
}