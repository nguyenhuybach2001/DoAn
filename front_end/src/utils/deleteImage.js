export const deleteImage = async (file) => {
  try {
    const res = await fetch(
      `http://localhost:8080/api/upload/image?url=${file}`,
      {
        method: "DELETE",
      }
    );

    const data = await res.json();
    if (data.status === "success") {
      return {
        success: true,
        message: "Image deleted successfully",
      };
    } else {
      return { success: false, error: "Delete failed" };
    }
  } catch (err) {
    return { success: false, error: err.message };
  }
};
