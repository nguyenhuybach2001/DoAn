import axiosClient from "./baseApi";

const paymentApi = {
  createPaymentIntent: async ({ amount, billId }) => {
    const params = new URLSearchParams();
    params.append("amount", amount);
    params.append("billId", billId);

    const response = await axiosClient.post(
      "/auth/create-payment-intent",
      params,
      {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      }
    );

    return response.data;
  },
};

export default paymentApi;
