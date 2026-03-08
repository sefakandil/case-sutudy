import React from "react";

class GlobalErrorBoundary extends React.Component {

  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error("React Global Error:", error, errorInfo);

    // buraya log servisi eklenebilir
    // sendToSentry(error)
  }

  render() {
    if (this.state.hasError) {
      return <h1>Bir hata oluştu.</h1>;
    }

    return this.props.children;
  }
}

export default GlobalErrorBoundary;