import { Navigate, Route, Routes } from "react-router-dom"
import LoginPage from "./pages/Auth/LoginPage"
import RegisterPage from "./pages/Auth/RegisterPage"
import ProtectedRoute from "./components/ProtectedRoute"
import Dashboard from "./pages/Dashboard"
import AppLayout from "./layouts/AppLayout"
import Tx from "./pages/Tx"
import Accounts from "./pages/Accounts"
import Customers from "./pages/Customers"

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/customers" element={<Customers />} />
        <Route path="/accounts" element={<Accounts />} />
        <Route path="/tx" element={<Tx />} />
      </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}