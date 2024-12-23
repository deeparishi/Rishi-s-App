import React from 'react';
import styles from './Register.module.css';
import FormInput from '../Common/FormInput';

const Register = () => {
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Register submitted');
  };

  return (
    <div className={styles.container}>
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <FormInput label="Name" type="text" name="name" />
        <FormInput label="Email" type="email" name="email" />
        <FormInput label="Password" type="password" name="password" />
        <button className={styles.button} type="submit">Register</button>
      </form>
    </div>
  );
};

export default Register;
