export default function Dashboard() {
  const token = localStorage.getItem("token")
  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <h1 className="text-3xl font-bold mb-4">Dashboard</h1>

      <p className="mt-2 text-white/70">
                Token: {token ? `${token.slice(0, 25)}...` : "Chưa đăng nhập"}
      </p>

      <button
        className="mt-6 rounded-xl bg-white/10 px-4 py-2 hover:bg-white/15"
        onClick={() => {
          localStorage.removeItem("token")
          window.location.href = "/login"
        }}
      >
        Logout
      </button>
    </div>
  )
}