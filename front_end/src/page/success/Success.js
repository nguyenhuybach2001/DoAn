"use client";
import authApi from "@/src/api/authApi";
import { addUser } from "@/src/redux/slices/authSlice";
import { Button, Form, Input } from "antd";
import { useRouter, useSearchParams } from "next/navigation";
import React, { useEffect } from "react";

export default function Success() {
  const router = useRouter();
  const { form } = Form.useForm();
  const searchParams = useSearchParams();
  const fixedEmail = searchParams.get("email")
    ? searchParams.get("email").replace(/ /g, "+")
    : "";

  const onSubmit = async (values) => {
    const res = await authApi.resetPassword(
      searchParams.get("token"),
      fixedEmail,
      values.confirmPassword
    );
    if (res) {
      localStorage.setItem("accessToken", res.accessToken);
      localStorage.setItem("refreshToken", res.refreshToken);
      const responsive = await authApi.getUserInfo();
      if (responsive) {
        dispatch(addUser(responsive));
      }
      router.push("/home");
    }
  };
  useEffect(() => {
    const fetchData = async () => {
      const res = await authApi.activateAccount(
        searchParams.get("token"),
        fixedEmail
      );
      if (res) {
        console.log(res);
      }
    };
    fetchData();
  }, []);
  return (
    <div className="flex justify-center items-center h-screen flex-col">
      <p className="font-bold text-2xl">
        Registration Successful! Set Your Password
      </p>
      <p>
        Your account has been created successfully! Please set a secure password
        to complete your registration.
      </p>
      <Form form={form} onFinish={onSubmit}>
        <Form.Item
          name="password"
          rules={[
            { required: true, message: "Please enter your password!" },
            { min: 6, message: "Password must be at least 6 characters!" },
          ]}
        >
          <Input.Password placeholder="Enter your password" />
        </Form.Item>

        <Form.Item
          name="confirmPassword"
          dependencies={["password"]}
          rules={[
            { required: true, message: "Please confirm your password!" },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue("password") === value) {
                  return Promise.resolve();
                }
                return Promise.reject("Passwords do not match!");
              },
            }),
          ]}
        >
          <Input.Password placeholder="Confirm your password" />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit">
            Submit
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}
