"use client";
import paymentApi from "@/src/api/paymentApi";
import {
  Elements,
  PaymentElement,
  useElements,
  useStripe,
} from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import { Button } from "antd";
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
    <form onSubmit={handleSubmit}>
      <PaymentElement />
      <Button htmlType="submit" disabled={!stripe || isLoading}>
        {isLoading ? "Đang xử lý..." : "Thanh toán"}
      </Button>
      {message && <div>{message}</div>}
    </form>
  );
}

export default function PaymentPage() {
  const [clientSecret, setClientSecret] = useState("");
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
    <div className="p-4 bg-white h-screen">
      <div className="grid grid-cols-2">
        <div>
          <p>
            {serviceNameMap[currentBill.service] || "Dịch vụ không xác định"}
          </p>
          <p>Thời gian: {currentBill.date}</p>
          <p>Phòng: {currentBill.room_number}</p>
          <p>Số tiền: {currentBill.amount}</p>
        </div>
        {clientSecret ? (
          <Elements stripe={stripePromise} options={options}>
            <CheckoutForm />
          </Elements>
        ) : (
          <p>Đang tải thông tin thanh toán...</p>
        )}
      </div>
    </div>
  );
}
