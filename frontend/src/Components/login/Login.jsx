import React from 'react';
import styles from './Login.module.css';
import FormInput from '../Common/FormInput';

const Login = () => {
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Login submitted');
  };

  return (
    <div className={styles.container}>
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <FormInput label="Email" type="email" name="email" />
        <FormInput label="Password" type="password" name="password" />
        <button className={styles.button} type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;
