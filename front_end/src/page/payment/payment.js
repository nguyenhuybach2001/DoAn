"use client";
import paymentApi from "@/src/api/paymentApi";
import { CloseOutlined } from "@ant-design/icons";
import {
  Elements,
  PaymentElement,
  useElements,
  useStripe,
} from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import { Button } from "antd";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";

const stripePromise = loadStripe(
  "pk_test_51RHQxP4cSzRrzxwccoQfy2r2bzS4KBVafprEQKODgLOdiO2Bozo1oLg0rXbXVwLOXTKzOOhNjwMWH6YvzZCF7AYi00FYzBwAza"
);

function CheckoutForm() {
  const stripe = useStripe();
  const elements = useElements();
  const [message, setMessage] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    setIsLoading(true);

    const { error } = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: "http://localhost:2001/payment-success",
      },
    });

    if (error) {
      setMessage(error.message);
    } else {
      setMessage("Đang xử lý thanh toán...");
    }

    setIsLoading(false);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <PaymentElement />
      <Button
        htmlType="submit"
        type="primary"
        loading={isLoading}
        disabled={!stripe || isLoading}
        className="w-full"
      >
        {isLoading ? "Đang xử lý..." : "Thanh toán ngay"}
      </Button>
      {message && <p className="text-center text-red-500">{message}</p>}
    </form>
  );
}

export default function PaymentPage() {
  const [clientSecret, setClientSecret] = useState("");
  const router = useRouter();
  const { currentBill } = useSelector((state) => state.serviceBill);
  useEffect(() => {
    const fetchClientSecret = async () => {
      try {
        const data = await paymentApi.createPaymentIntent({
          amount: currentBill.amount,
          billId: currentBill.billId,
        });
        setClientSecret(data.clientSecret);
      } catch (error) {
        console.error("Lỗi khi lấy client secret:", error);
      }
    };
    if (currentBill != null) {
      fetchClientSecret();
    }
  }, [currentBill]);

  const options = {
    clientSecret,
    appearance: { theme: "stripe" },
  };
  const serviceNameMap = {
    RENT: "Tiền nhà",
    ELECTRIC: "Dịch vụ điện",
    WATER: "Dịch vụ nước",
    CLEANING: "Dịch vụ vệ sinh",
    INTERNET: "Dịch vụ internet",
    PARKING: "Dịch vụ đỗ xe",
    OTHERS: "Dịch vụ khác",
  };
  return (
    <div className=" bg-gray-100 flex items-center justify-center p-6">
      <div className="bg-gray-300 rounded-full w-10 flex justify-center items-center h-10 border-2 border-solid absolute top-5 left-5 cursor-pointer">
        <CloseOutlined
          className="font-bold text-xl"
          onClick={() => {
            router.push("/home");
          }}
        />
      </div>
      <div className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-3xl">
        <h2 className="text-2xl font-semibold mb-6 text-center text-gray-800">
          Xác nhận thanh toán
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Thông tin hóa đơn */}
          <div className="space-y-3 text-gray-700">
            <p>
              <strong>Dịch vụ:</strong>{" "}
              {serviceNameMap[currentBill?.service] || "Dịch vụ không xác định"}
            </p>
            <p>
              <strong>Thời gian:</strong> {currentBill?.date}
            </p>
            <p>
              <strong>Phòng:</strong> {currentBill?.room_number}
            </p>
            <p>
              <strong>Số tiền:</strong>{" "}
              {Number(currentBill?.amount).toLocaleString()} VND
            </p>
          </div>

          {/* Form thanh toán */}
          <div className="bg-gray-50 p-4 rounded-xl border">
            {clientSecret ? (
              <Elements stripe={stripePromise} options={options}>
                <CheckoutForm />
              </Elements>
            ) : (
              <p className="text-center">Đang tải thông tin thanh toán...</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
