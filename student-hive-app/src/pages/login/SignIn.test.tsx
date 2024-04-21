import { render, fireEvent, waitFor, screen } from '@testing-library/react';
import SignIn from './SignIn';
import AuthService from "../../services/auth/AuthService";

jest.mock("axios");
jest.mock("../../services/auth/AuthService");

describe('SignIn Component', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    it('renders without crashing', () => {
        render(<SignIn />);
    });

    it('allows user input and submission', async () => {
        render(<SignIn />);

        const emailInput = screen.getByLabelText('Email Address');
        const passwordInput = screen.getByLabelText('Password');
        const submitButton = screen.getByText('Sign In');

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password' } });
        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(AuthService.signIn).toHaveBeenCalledWith('test@example.com', 'password');
        });
    });

    it('handles successful sign-in', async () => {
        render(<SignIn />);

        const emailInput = screen.getByLabelText('Email Address');
        const passwordInput = screen.getByLabelText('Password');
        const submitButton = screen.getByText('Sign In');

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password' } });
        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(window.location.href).toBe('/');
        });
    });

    it('displays error message on sign-in failure', async () => {
        const errorMessage = 'Invalid credentials';
        jest.spyOn(AuthService, 'signIn').mockRejectedValueOnce(new Error(errorMessage));

        render(<SignIn />);

        const emailInput = screen.getByLabelText('Email Address');
        const passwordInput = screen.getByLabelText('Password');
        const submitButton = screen.getByText('Sign In');

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password' } });
        fireEvent.click(submitButton);

        await waitFor(() => {
            expect(screen.getByText(errorMessage)).toBeInTheDocument();
        });
    });

    it('handles form submission correctly', async () => {
        render(<SignIn />);

        const emailInput = screen.getByLabelText('Email Address');
        const passwordInput = screen.getByLabelText('Password');
        const submitButton = screen.getByText('Sign In');

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password' } });
        // @ts-ignore
        // eslint-disable-next-line testing-library/no-node-access
        fireEvent.submit(submitButton.closest('form'));

        await waitFor(() => {
            expect(AuthService.signIn).toHaveBeenCalledWith('test@example.com', 'password');
        });
    });

    // Add more tests as needed
});
