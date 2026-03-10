import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { http } from "../services/http";
import { clearToken } from "../services/auth";

type AccountDto = {
  id: number;
  accountNo: string;
  balanceSnapshot: number;
  frozenSnapshot: number;
  currency: string;
  customerEntity?: { fullName?: string };
};

export default function Dashboard() {
  const nav = useNavigate();
  const [accountId] = useState<number>(1); // ✅ đổi id account thật
  const [account, setAccount] = useState<AccountDto | null>(null);
  const [err, setErr] = useState("");

  const fmtMoney = (v: any, ccy?: string) => {
    if (v === null || v === undefined) return "-";
    const n = typeof v === "number" ? v : Number(v);
    if (Number.isNaN(n)) return String(v);
    return new Intl.NumberFormat("vi-VN").format(n) + (ccy ? ` ${ccy}` : "");
  };

  const available = useMemo(() => {
    if (!account) return null;
    return (account.balanceSnapshot ?? 0) - (account.frozenSnapshot ?? 0);
  }, [account]);

  const loadAccount = async () => {
    setErr("")
    try {
      const res = await http.get<AccountDto>("/api/account")
      setAccount(res.data)
    } catch (e: any) {
      setErr(e?.response?.data?.message || "Load account failed")
    }
  }

  useEffect(() => {
    loadAccount();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountId]);

  const logout = () => {
    clearToken();
    nav("/login");
  };

  return (
    <div className="min-h-screen p-6 space-y-4">
      <div className="rounded-xl border bg-white p-4 text-slate-900 shadow-sm">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="space-y-2">
            <div>
              <div className="text-sm text-gray-500">Số tài khoản (STK)</div>
              <div className="text-xl font-semibold">
                {account?.accountNo ? account.accountNo : "Đang tải..."}
              </div>
              {account?.customerEntity?.fullName && (
                <div className="text-sm text-gray-600">
                  Chủ tài khoản: <span className="font-medium">{account.customerEntity.fullName}</span>
                </div>
              )}
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div className="rounded-lg border p-3">
                <div className="text-xs text-gray-500">Số dư</div>
                <div className="text-lg font-semibold">
                  {fmtMoney(account?.balanceSnapshot, account?.currency)}
                </div>
              </div>

              <div className="rounded-lg border p-3">
                <div className="text-xs text-gray-500">Đang giữ</div>
                <div className="text-lg font-semibold">
                  {fmtMoney(account?.frozenSnapshot, account?.currency)}
                </div>
              </div>

              <div className="rounded-lg border p-3">
                <div className="text-xs text-gray-500">Khả dụng</div>
                <div className="text-lg font-semibold">
                  {fmtMoney(available, account?.currency)}
                </div>
              </div>
            </div>

            {err && <div className="text-sm text-red-500">{err}</div>}
          </div>

          <div className="flex gap-2">
            <button
              onClick={loadAccount}
              className="px-4 py-2 rounded-lg border bg-black text-white"
            >
              Reload
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}