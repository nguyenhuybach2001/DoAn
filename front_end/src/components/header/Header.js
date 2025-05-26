"use client";
import {
  Avatar,
  Button,
  DatePicker,
  Dropdown,
  Form,
  Input,
  Menu,
  message,
  Modal,
} from "antd";
import Image from "next/image";
import React, { useEffect, useState } from "react";
import logo from "@/asset/images/logo.png";
import { usePathname, useRouter } from "next/navigation";
import { useDispatch, useSelector } from "react-redux";
import { closeLogin, openLogin } from "@/src/redux/slices/modalSlice";
import authApi from "@/src/api/authApi";
import { addUser } from "@/src/redux/slices/authSlice";
import { BellOutlined, UserOutlined } from "@ant-design/icons";
import { addDataProvince } from "@/src/redux/slices/provincesSlice";
import { connectNotificationSocket } from "@/src/utils/websocket";
import notificationApi from "@/src/api/notificationApi";
import serviceBillApi from "@/src/api/serviceBillApi";
import { fetchServiceBillById } from "@/src/redux/slices/serviceBillSlice";

export default function Header() {
  const pathName = usePathname();
  const router = useRouter();
  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const [step, setStep] = useState(0);
  const [modalUser, setModalUser] = useState(false);
  const [modalPayment, setModalPayment] = useState(false);
  const [modalEditInfo, setModalEditInfo] = useState(false);
  const [modalChangePassword, setModalChangePassword] = useState(false);
  const [formEditInfo] = Form.useForm();
  const [formPassword] = Form.useForm();
  const { modalLogin } = useSelector((state) => state.modal);
  const { user } = useSelector((state) => state.auth);
  const [countdown, setCountdown] = useState(0);
  const [isCounting, setIsCounting] = useState(false);
  const [errorMess, setErrorMess] = useState("");
  const { dataProvince } = useSelector((state) => state.province);
  const [notifications, setNotifications] = useState([]);
  const fetchNotifications = async () => {
    const res = await notificationApi.getAll(user.id);
    setNotifications(res);
  };
  const { currentBill, loading } = useSelector((state) => state.serviceBill);

  useEffect(() => {
    if (!user) return;
    fetchNotifications();
    const client = connectNotificationSocket(user.id, (notification) => {
      // setNotifications((prev) => [notification, ...prev]);
      fetchNotifications();
      message.success("You have a new notification!");
    });

    return () => {
      client.deactivate(); // Cleanup khi component bị unmount
    };
  }, [user]);

  useEffect(() => {
    let timer;
    if (step === 2 && isCounting) {
      timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            setIsCounting(false);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isCounting, step]);
  const fetchLogin = async () => {
    const user_token = localStorage.getItem("accessToken");
    if (user_token) {
      const responsive = await authApi.getUserInfo();
      if (responsive) {
        dispatch(closeLogin());
        dispatch(addUser(responsive));
      }
    }
  };
  useEffect(() => {
    fetchLogin();
  }, []);
  const convertToCascaderFormat = (data) => {
    return data.map((province) => ({
      value: province.codename,
      label: province.name,
      children: province.districts.map((district) => ({
        value: district.codename,
        label: district.name,
        children: district.wards.map((ward) => ({
          value: ward.codename,
          label: ward.name,
        })),
      })),
    }));
  };
  useEffect(() => {
    if (!dataProvince) {
      fetch("https://provinces.open-api.vn/api/?depth=3")
        .then((res) => res.json())
        .then((data) => {
          const formattedData = convertToCascaderFormat(data);
          dispatch(addDataProvince(formattedData));
        })
        .catch((err) => {
          console.error("Error fetching provinces data:", err);
        });
    }
  }, [dataProvince]);
  useEffect(() => {
    if (step === 2) {
      setCountdown(30);
      setIsCounting(true);
    }
  }, [step]);
  const showModal = () => {
    dispatch(openLogin());
  };
  const handleCancel = () => {
    dispatch(closeLogin());
    setStep(0);
    form.resetFields();
  };
  const items = [
    {
      label: (
        <p
          onClick={() => {
            setModalUser(true);
          }}
        >
          Profile
        </p>
      ),
      key: "0",
    },
    user?.role == "CUSTOMER" && {
      label: (
        <p
          onClick={() => {
            router.push("/my-rental");
          }}
        >
          My Rentals
        </p>
      ),
      key: "2",
    },
    {
      label: (
        <p
          onClick={() => {
            router.push("/home");
            dispatch(addUser(null));
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
          }}
        >
          Log out
        </p>
      ),
      key: "1",
    },
  ];
  const listTab = [
    {
      name: "Home",
      active: pathName === "/home",
      link: "/home",
    },

    {
      name: "Dashboard",
      active: pathName === "/dashboard",
      link: "/dashboard",
    },
    {
      name: "About us",
      active: pathName === "/about",
      link: "/active",
    },
  ];
  const listTabUser = [
    {
      name: "Home",
      active: pathName === "/home",
      link: "/home",
    },
    {
      name: "About us",
      active: pathName === "/about",
      link: "/active",
    },
  ];

  const onLogin = async (values) => {
    try {
      const res = await authApi.login(values.username, values.password);
      if (res) {
        const responsive = await authApi.getUserInfo();
        if (responsive) {
          dispatch(closeLogin());
          dispatch(addUser(responsive));
          form.resetFields();
          responsive.isPasswordChanged == false && setModalChangePassword(true);
        }
      }
    } catch (error) {
      const message =
        error?.response?.data?.error || "Đăng nhập thất bại, thử lại sau!";
      console.error("Login error:", message);
    }
  };
  const onForgetPassword = async (values) => {
    const res = await authApi.register(values.fullName, values.email);
    if (res.status == "success") {
      setStep(2);
    } else {
      setErrorMess(res.message);
    }
  };
  const handleResend = () => {
    setCountdown(30);
    setIsCounting(true);
  };
  const renderModal = () => {
    switch (step) {
      case 0:
        return (
          <Form form={form} name="basic" onFinish={onLogin} className="px-4">
            <p className="text-3xl font-bold my-6">Login</p>
            <Form.Item
              label={null}
              name="username"
              rules={[
                {
                  validator: (_, value) => {
                    if (!value) {
                      return Promise.reject(
                        "Please input your email or phone number!"
                      );
                    }

                    const phoneRegex = /^[0-9]{10,11}$/;
                    if (/^\d+$/.test(value)) {
                      return phoneRegex.test(value)
                        ? Promise.resolve()
                        : Promise.reject("Invalid phone number format!");
                    }

                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    return emailRegex.test(value)
                      ? Promise.resolve()
                      : Promise.reject("Invalid email format!");
                  },
                },
              ]}
            >
              <Input placeholder="Email or Phone number" />
            </Form.Item>
            <Form.Item
              label={null}
              name="password"
              rules={[
                {
                  required: true,
                  message: "Please input your password!",
                },
              ]}
            >
              <Input.Password placeholder="Password" />
            </Form.Item>
            <Form.Item label={null}>
              <Button className="w-full" type="primary" htmlType="submit">
                Login
              </Button>
              <div className="flex justify-end">
                <p
                  onClick={() => {
                    setStep(1);
                  }}
                  className="text-blue-500 border-b-[1px] border-blue-500 w-fit cursor-pointer"
                >
                  Forgot Password
                </p>
              </div>
            </Form.Item>
          </Form>
        );
      case 1:
        return (
          <Form form={form} onFinish={onForgetPassword} className="px-4">
            <p className="text-3xl font-bold my-6">Forget Password</p>
            <Form.Item
              label={null}
              name="email"
              rules={[
                {
                  validator: (_, value) => {
                    if (!value) {
                      return Promise.reject("Please input your email!");
                    }
                    if (errorMess) {
                      return Promise.reject(errorMess);
                    }
                    const phoneRegex = /^[0-9]{10,11}$/;
                    if (/^\d+$/.test(value)) {
                      return phoneRegex.test(value)
                        ? Promise.resolve()
                        : Promise.reject("Invalid phone number format!");
                    }
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    return emailRegex.test(value)
                      ? Promise.resolve()
                      : Promise.reject("Invalid email format!");
                  },
                },
              ]}
            >
              <Input placeholder="Enter your email address or your phone number" />
            </Form.Item>
            <Form.Item label={null}>
              <Button className="w-full" type="primary" htmlType="submit">
                Continue
              </Button>
            </Form.Item>
            <p className="text-black">
              Already have an account?{" "}
              <span
                onClick={() => setStep(0)}
                className="text-blue-500 font-bold border-b-[1px] border-blue-500 w-fit cursor-pointer"
              >
                Login
              </span>
            </p>
          </Form>
        );
      case 2:
        return (
          <div>
            <p className="text-3xl font-bold my-6">Registration Successful</p>
            <p>
              We have sent a confirmation email to
              <span className="text-blue-500">
                {" "}
                {form.getFieldValue("email")}
              </span>
              .{" "}
            </p>
            <p>
              Please check your inbox and follow the instructions to complete
              your registration.
            </p>
            <p>Didn't receive the email? </p>
            <div className="flex gap-3">
              <Button
                onClick={() => {
                  handleResend();
                }}
                className="w-32"
                disabled={countdown > 0}
              >
                {countdown > 0 ? `${countdown}s` : "Resend Email"}
              </Button>
              <Button
                onClick={() => {
                  setStep(1);
                }}
              >
                Change Email
              </Button>
            </div>
            {/* <p>
              Already verified?{" "}
              <span
                className="text-blue-500 font-bold border-b-[1px] border-blue-500 w-fit cursor-pointer"
                onClick={() => {
                  setStep(0);
                  form.setFieldValue("username", form.getFieldValue("email"));
                }}
              >
                Login here
              </span>
            </p> */}
          </div>
        );
      default:
        break;
    }
  };
  const getBill = (billId) => {
    dispatch(fetchServiceBillById(billId));
    setModalPayment(true);
  };
  const checkNotification = async (id) => {
    const res = await notificationApi.markAsRead(id);
    if (res.status == "success") {
      setNotifications((prev) =>
        prev.map((noti) => {
          if (noti.id == id) {
            return { ...noti, status: "READ" };
          }
          return noti;
        })
      );
    }
  };
  const itemsNotification = notifications.map((noti, index) => ({
    key: index,
    label: (
      <div
        onClick={() => {
          checkNotification(noti.id);
          noti.serviceId && getBill(noti.serviceId);
        }}
        className={`px-2 py-1 ${
          noti.status == "UNREAD" ? "hover:bg-gray-100" : "bg-gray-100"
        }`}
      >
        <p
          className={`font-semibold ${
            noti.status == "READ" ? "text-[#858585]" : ""
          }`}
        >
          {noti.message}
        </p>
        {/* <p className="text-xs text-gray-500">{noti.description}</p> */}
      </div>
    ),
  }));
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
    !pathName.includes("success") && (
      <div className=" flex justify-between items-center p-4 bg-white shadow-md px-16 z-20 ">
        <Image
          src={logo}
          alt="logo"
          onClick={() => router.push("/home")}
          className="w-16 cursor-pointer"
        />
        <div className=" flex gap-5 justify-center items-center">
          {(user?.role == "LANDLORD" || user?.role == "STAFF"
            ? listTab
            : listTabUser
          ).map((tab) => (
            <p
              key={tab.name}
              className={`${
                tab.active
                  ? "text-blue-500 border-b-2 border-blue-500"
                  : "text-black"
              } cursor-pointer font-bold`}
              onClick={() => {
                router.push(tab.link);
              }}
            >
              {tab.name}
            </p>
          ))}
        </div>
        {user ? (
          <div className="flex gap-5 items-center">
            <Dropdown menu={{ items }} trigger={["click"]}>
              <div className="flex gap-2 items-center cursor-pointer">
                <p>{user.fullName}</p>
                <Avatar icon={<UserOutlined />} />
              </div>
            </Dropdown>
            <div className="relative cursor-pointer ">
              <Dropdown
                menu={{
                  items: itemsNotification,
                }}
                trigger={["click"]}
              >
                <BellOutlined className="text-2xl" />
              </Dropdown>
              {notifications.filter((val) => val.status == "UNREAD").length >
                0 && (
                <p className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-sm">
                  {notifications.filter((val) => val.status == "UNREAD").length}
                </p>
              )}
            </div>
          </div>
        ) : (
          <Button onClick={showModal}>Log in</Button>
        )}
        <Modal
          open={modalLogin}
          footer={false}
          width={500}
          onCancel={handleCancel}
        >
          {renderModal()}
        </Modal>
        <Modal
          open={modalUser}
          footer={false}
          onCancel={() => setModalUser(false)}
          width={600}
        >
          <div className="p-4 space-y-4">
            <h2 className="text-xl font-bold text-center">
              Thông tin người dùng
            </h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-gray-500">Họ tên</p>
                <p>{user?.fullName || "Chưa cập nhật"}</p>
              </div>
              <div>
                <p className="text-gray-500">Email</p>
                <p>{user?.email || "Chưa cập nhật"}</p>
              </div>
              <div>
                <p className="text-gray-500">Số điện thoại</p>
                <p>{user?.phoneNumber || "Chưa cập nhật"}</p>
              </div>
              <div>
                <p className="text-gray-500">Số CMND/CCCD</p>
                <p>{user?.identityNumber || "Chưa cập nhật"}</p>
              </div>
              <div>
                <p className="text-gray-500">Ngày sinh</p>
                <p>{user?.dateOfBirth || "Chưa cập nhật"}</p>
              </div>
              <div className="col-span-2">
                <p className="text-gray-500">Địa chỉ</p>
                <p>{user?.address || "Chưa cập nhật"}</p>
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-4">
              <Button
                type="default"
                onClick={() => {
                  setModalUser(false);
                  setModalChangePassword(true);
                }}
              >
                Đổi mật khẩu
              </Button>
              <Button
                type="primary"
                onClick={() => {
                  setModalUser(false);
                  setModalEditInfo(true);
                }}
              >
                Chỉnh sửa thông tin
              </Button>
            </div>
          </div>
        </Modal>

        {currentBill != null && (
          <Modal
            loading={loading}
            open={modalPayment}
            footer={false}
            onCancel={() => {
              setModalPayment(false);
            }}
          >
            <p>
              {serviceNameMap[currentBill.service] || "Dịch vụ không xác định"}
            </p>
            <p>Thời gian: {currentBill.date}</p>
            <p>Phòng: {currentBill.room_number}</p>
            <p>Số tiền: {currentBill.amount}</p>

            <Button
              onClick={() => {
                router.push("/payment");
                setModalPayment(false);
              }}
            >
              Thanh toán ngay
            </Button>
          </Modal>
        )}
        <Modal
          open={modalChangePassword}
          title="Đổi mật khẩu"
          onCancel={() => setModalChangePassword(false)}
          onOk={() => formPassword.submit()}
        >
          <Form
            form={formPassword}
            layout="vertical"
            onFinish={async (values) => {
              if (values.newPassword !== values.confirmPassword) {
                message.error("Mật khẩu xác nhận không khớp!");
                return;
              }
              const res = await authApi.resetPassword(values);
              if (res) {
                // Gọi API đổi mật khẩu
                console.log("Đổi mật khẩu:", res);
                setModalChangePassword(false);
              }
            }}
          >
            <Form.Item
              name="oldPassword"
              label="Mật khẩu hiện tại"
              rules={[{ required: true }]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item
              name="newPassword"
              label="Mật khẩu mới"
              rules={[
                { required: true, message: "Vui lòng nhập mật khẩu mới" },
                () => ({
                  validator(_, value) {
                    if (!value) {
                      return Promise.reject(
                        new Error("Vui lòng nhập mật khẩu mới")
                      );
                    }

                    const passwordRegex =
                      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
                    if (!passwordRegex.test(value)) {
                      return Promise.reject(
                        new Error(
                          "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
                        )
                      );
                    }

                    return Promise.resolve();
                  },
                }),
              ]}
            >
              <Input.Password />
            </Form.Item>
            <Form.Item
              name="confirmPassword"
              label="Xác nhận mật khẩu mới"
              dependencies={["newPassword"]}
              rules={[
                { required: true, message: "Vui lòng xác nhận mật khẩu" },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue("newPassword") === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      new Error("Mật khẩu xác nhận không khớp")
                    );
                  },
                }),
              ]}
            >
              <Input.Password />
            </Form.Item>
          </Form>
        </Modal>
        <Modal
          open={modalEditInfo}
          title="Chỉnh sửa thông tin"
          onCancel={() => setModalEditInfo(false)}
          onOk={() => formEditInfo.submit()}
        >
          <Form
            layout="vertical"
            form={formEditInfo}
            initialValues={{
              fullName: user?.fullName,
              email: user?.email,
              phoneNumber: user?.phoneNumber,
              identityNumber: user?.identityNumber,
              address: user?.address,
              dateOfBirth: user?.dateOfBirth,
            }}
            onFinish={async (values) => {
              const res = await authApi.updateUserInfo(values);
              if (res) {
                console.log("Update info:", res);
                setModalEditInfo(false);
                fetchLogin();
              }
            }}
          >
            <Form.Item
              name="fullName"
              label="Họ tên"
              rules={[{ required: true, message: "Vui lòng nhập họ tên" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item name="email" label="Email">
              <Input disabled />
            </Form.Item>
            <Form.Item name="phoneNumber" label="Số điện thoại">
              <Input />
            </Form.Item>
            <Form.Item name="identityNumber" label="CMND/CCCD">
              <Input />
            </Form.Item>
            <Form.Item name="address" label="Địa chỉ">
              <Input />
            </Form.Item>
            <Form.Item name="dateOfBirth" label="Ngày sinh">
              <DatePicker format="YYYY-MM-DD" className="w-full" />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    )
  );
}
