import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

export default function HomePage() {
  return (
    <div className="page-shell">
      <Navbar />

      <main className="home-content">
        <section className="hero">
          <h1>Book Your Next Cut</h1>

          <p>
            Find available barbers and book appointments
            <br />
            in a few clicks.
          </p>

          <Link to="/slots" className="primary-button">
            Book Appointment
          </Link>
        </section>
      </main>
    </div>
  );
}