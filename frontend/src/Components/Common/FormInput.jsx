import React from 'react';
import styles from './FormInput.module.css';

const FormInput = ({ label, type, name }) => {
  return (
    <div className={styles.inputGroup}>
      <label htmlFor={name}>{label}</label>
      <input type={type} id={name} name={name} required />
    </div>
  );
};

export default FormInput;
